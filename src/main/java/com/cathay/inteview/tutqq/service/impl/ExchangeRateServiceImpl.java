package com.cathay.inteview.tutqq.service.impl;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.constants.ExchangeRateProviderName;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;
import com.cathay.inteview.tutqq.mapper.ExchangeRateMapper;
import com.cathay.inteview.tutqq.property.ExchangeRateSyncProperties;
import com.cathay.inteview.tutqq.repository.CurrencyPairRepository;
import com.cathay.inteview.tutqq.repository.DataProviderRepository;
import com.cathay.inteview.tutqq.repository.ExchangeRateRepository;
import com.cathay.inteview.tutqq.service.CurrencyService;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import com.cathay.inteview.tutqq.service.provider.ExchangeRateProvider;
import com.cathay.inteview.tutqq.service.provider.ExchangeRateProviderFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);
    private static final int MAX_DATE_RANGE_DAYS = 180; // 6 months
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyPairRepository currencyPairRepository;
    private final ExchangeRateMapper exchangeRateMapper;
    private final DataProviderRepository dataProviderRepository;
    private final CurrencyService currencyService;
    private final ExchangeRateSyncProperties syncProperties;
    private final ExchangeRateProviderFactory providerFactory;

    @Override
    @Cacheable(value = "exchangeRates", key = "#baseCurrency + '_' + #quoteCurrency + '_' + #startDate + '_' + #endDate")
    public List<ExchangeRateDto> getExchangeRates(
            String baseCurrency,
            String quoteCurrency,
            LocalDate startDate,
            LocalDate endDate
    ) throws CurrencyPairNotFoundException, DateRangeExceededException {

        logger.debug("Fetching exchange rates for {}/{} from {} to {}",
                baseCurrency, quoteCurrency, startDate, endDate);

        // Validation
        validateDateRange(startDate, endDate);
        validateCurrencyPair(baseCurrency, quoteCurrency);

        // Convert to Instant for database query
        var startInstant = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        var endInstant = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        // Fetch data
        List<ExchangeRate> exchangeRates = exchangeRateRepository
                .findExchangeRatesByCurrencyPairAndDateRange(
                        baseCurrency, quoteCurrency, startInstant, endInstant);

        // Map to DTOs
        List<ExchangeRateDto> exchangeRateDataList = exchangeRateMapper.toDtoList(exchangeRates);

        logger.debug("Successfully fetched {} exchange rates", exchangeRateDataList.size());

        return exchangeRateDataList;
    }

    @Override
    public void validateDateRange(LocalDate startDate, LocalDate endDate) throws DateRangeExceededException {
        if (startDate.isAfter(endDate)) {
            throw new DateRangeExceededException(
                    String.format("End date (%s) must be after start date (%s)", endDate, startDate),
                    startDate, endDate, 0, MAX_DATE_RANGE_DAYS, 6
            );
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > MAX_DATE_RANGE_DAYS) {
            throw new DateRangeExceededException(
                    String.format("Maximum allowed date range is 6 months. Requested range is %d days.", daysBetween),
                    startDate, endDate, (int) daysBetween, MAX_DATE_RANGE_DAYS, 6
            );
        }
    }

    @Override
    public void validateCurrencyPair(String baseCurrency, String quoteCurrency) throws CurrencyPairNotFoundException {
        CurrencyPair currencyPair = currencyPairRepository
                .findByBaseCurrencyCodeAndQuoteCurrencyCode(baseCurrency, quoteCurrency)
                .orElseThrow(() -> new CurrencyPairNotFoundException(
                        String.format("Currency pair %s/%s is not supported", baseCurrency, quoteCurrency),
                        baseCurrency, quoteCurrency
                ));

        if (!currencyPair.getIsActive()) {
            throw new CurrencyPairNotFoundException(
                    String.format("Currency pair %s/%s is currently inactive", baseCurrency, quoteCurrency),
                    baseCurrency, quoteCurrency
            );
        }
    }

    @Override
    public void syncExchangeRates() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(syncProperties.getDefaultDaysBack());

            List<String> currencyCodes = currencyService.listCurrencies(true, 0, Integer.MAX_VALUE, null)
                    .getContent()
                    .stream()
                    .map(CurrencyDto::getCode)
                    .toList();

            List<String[]> currencyPairs = currencyCodes.stream()
                    .flatMap(base -> currencyCodes.stream()
                            .filter(quote -> !quote.equals(base))
                            .map(quote -> new String[]{base, quote}))
                    .toList();

            currencyPairs.forEach(pair -> {
                try {
                    String baseCurrency = pair[0];
                    String quoteCurrency = pair[1];

                    logger.info("Starting sync for {} to {} from {} to {}", baseCurrency, quoteCurrency, startDate, endDate);

                    // Call external API
                    ExchangeRateProvider provider = providerFactory.getProvider(ExchangeRateProviderName.OANDA);

                    ExchangeRateApiResponse response = provider.getExchangeRates(
                            baseCurrency, quoteCurrency,
                            startDate,
                            endDate
                    );

                    if (response != null && response.getResponse() != null && !response.getResponse().isEmpty()) {

                        // Get or create currency pair
                        CurrencyPair currencyPair = getOrCreateCurrencyPair(baseCurrency, quoteCurrency);

                        // Get data provider
                        DataProvider dataProvider = getOrCreateDataProvider("Exchange Rates API", provider.getApiBaseUrl());

                        // Process each rate data point
                        for (ExchangeRateApiResponse.ExchangeRateData data : response.getResponse()) {
                            processExchangeRateData(data, currencyPair, dataProvider);
                        }

                        // Update provider's last sync time
                        dataProvider.setLastSyncAt(Instant.now());
                        dataProviderRepository.save(dataProvider);

                        logger.info("Successfully synced {} records for {}/{}",
                                response.getResponse().size(), baseCurrency, quoteCurrency);

                    } else {
                        logger.warn("No data received from API for {}/{}", baseCurrency, quoteCurrency);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                            String.format("Error syncing %s/%s: %s", pair[0], pair[1], e.getMessage()), e
                    );
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to sync exchange rates", e);
        }
    }

    private void processExchangeRateData(ExchangeRateApiResponse.ExchangeRateData data,
                                         CurrencyPair currencyPair, DataProvider provider) {
        try {
            ExchangeRate entity = exchangeRateMapper.toEntity(data, currencyPair, provider);

            // Save the exchange rate
            exchangeRateRepository.save(entity);

        } catch (Exception e) {
            logger.error("Error processing exchange rate data: {}", e.getMessage(), e);
        }
    }

    private CurrencyPair getOrCreateCurrencyPair(String baseCurrency, String quoteCurrency) {
        Optional<CurrencyPair> existing = currencyPairRepository
                .findByBaseCurrencyCodeAndQuoteCurrencyCode(baseCurrency, quoteCurrency);

        if (existing.isPresent()) {
            return existing.get();
        }


        // Create new currency pair
        CurrencyPair currencyPair = new CurrencyPair(new Currency(baseCurrency), new Currency(quoteCurrency));
        return currencyPairRepository.save(currencyPair);
    }

    private DataProvider getOrCreateDataProvider(String name, String apiEndpoint) {
        Optional<DataProvider> existing = dataProviderRepository.findByName(name);

        if (existing.isPresent()) {
            return existing.get();
        }

        // Create new data provider
        DataProvider provider = new DataProvider(name, apiEndpoint);
        return dataProviderRepository.save(provider);
    }
}
