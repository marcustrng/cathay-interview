package com.cathay.inteview.tutqq.service.impl;

import com.cathay.interview.tutqq.model.DateRange;
import com.cathay.interview.tutqq.model.ExchangeRateData;
import com.cathay.interview.tutqq.model.ExchangeRateResponse;
import com.cathay.interview.tutqq.model.PaginationInfo;
import com.cathay.interview.tutqq.model.ResponseMetadata;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;
import com.cathay.inteview.tutqq.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import com.cathay.inteview.tutqq.mapper.ExchangeRateMapper;
import com.cathay.inteview.tutqq.repository.CurrencyPairRepository;
import com.cathay.inteview.tutqq.repository.DataProviderRepository;
import com.cathay.inteview.tutqq.repository.ExchangeRateRepository;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private static final String API_BASE_URL = "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies";

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyPairRepository currencyPairRepository;
    private final ExchangeRateMapper exchangeRateMapper;
    private final DataProviderRepository dataProviderRepository;
    private final RestTemplate restTemplate;

    @Override
    @Cacheable(value = "exchangeRates", key = "#baseCurrency + '_' + #quoteCurrency + '_' + #startDate + '_' + #endDate + '_' + #pageable.pageNumber")
    public ExchangeRateResponse getExchangeRates(
            String baseCurrency,
            String quoteCurrency,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
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
        Page<ExchangeRate> exchangeRatesPage = exchangeRateRepository
                .findExchangeRatesByCurrencyPairAndDateRange(
                        baseCurrency, quoteCurrency, startInstant, endInstant, pageable);

        // Map to DTOs
        List<ExchangeRateData> exchangeRateDataList = exchangeRateMapper
                .toDtoList(exchangeRatesPage.getContent());

        // Build response
        var metadata = new ResponseMetadata(
                (int) exchangeRatesPage.getTotalElements(),
                exchangeRateDataList.size(),
                ResponseMetadata.GranularityEnum.DAILY,
                new DateRange(startDate, endDate)
        );

        var pagination = new PaginationInfo(
                pageable.getPageSize(),
                (int) pageable.getOffset(),
                exchangeRatesPage.hasNext()
        );

        URI nextUrl = exchangeRatesPage.hasNext()
                ? URI.create(buildNextUrl(baseCurrency, quoteCurrency, startDate, endDate, pageable))
                : null;

        URI prevUrl = exchangeRatesPage.hasPrevious()
                ? URI.create(buildPrevUrl(baseCurrency, quoteCurrency, startDate, endDate, pageable))
                : null;

        pagination.setNextUrl(JsonNullable.of(nextUrl));
        pagination.setPrevUrl(JsonNullable.of(prevUrl));


        logger.debug("Successfully fetched {} exchange rates", exchangeRateDataList.size());

        return new ExchangeRateResponse(exchangeRateDataList, metadata, pagination);
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
    public void syncExchangeRates(String baseCurrency, String quoteCurrency, String startDate, String endDate) {
        try {
            logger.info("Starting sync for {} to {} from {} to {}", baseCurrency, quoteCurrency, startDate, endDate);

            // Build API URL
            String url = String.format("%s?base=%s&quote=%s&data_type=chart&start_date=%s&end_date=%s",
                    API_BASE_URL, baseCurrency, quoteCurrency, startDate, endDate);

            // Call external API
            ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);

            if (response != null && response.getResponse() != null && !response.getResponse().isEmpty()) {

                // Get or create currency pair
                CurrencyPair currencyPair = getOrCreateCurrencyPair(baseCurrency, quoteCurrency);

                // Get data provider
                DataProvider provider = getOrCreateDataProvider("Exchange Rates API", API_BASE_URL);

                // Process each rate data point
                for (ExchangeRateApiResponse.ExchangeRateData data : response.getResponse()) {
                    processExchangeRateData(data, currencyPair, provider);
                }

                // Update provider's last sync time
                provider.setLastSyncAt(Instant.now());
                dataProviderRepository.save(provider);

                logger.info("Successfully synced {} records for {}/{}",
                        response.getResponse().size(), baseCurrency, quoteCurrency);

            } else {
                logger.warn("No data received from API for {}/{}", baseCurrency, quoteCurrency);
            }

        } catch (Exception e) {
            logger.error("Error syncing exchange rates for {}/{}: {}", baseCurrency, quoteCurrency, e.getMessage(), e);
            throw new RuntimeException("Failed to sync exchange rates", e);
        }
    }


    private String buildNextUrl(String baseCurrency, String quoteCurrency, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return String.format("/api/v1/exchange-rates/%s/%s?start_date=%s&end_date=%s&limit=%d&offset=%d",
                baseCurrency, quoteCurrency, startDate, endDate, pageable.getPageSize(), pageable.getOffset() + pageable.getPageSize());
    }

    private String buildPrevUrl(String baseCurrency, String quoteCurrency, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        long prevOffset = Math.max(0, pageable.getOffset() - pageable.getPageSize());
        return String.format("/api/v1/exchange-rates/%s/%s?start_date=%s&end_date=%s&limit=%d&offset=%d",
                baseCurrency, quoteCurrency, startDate, endDate, pageable.getPageSize(), prevOffset);
    }

    private void processExchangeRateData(ExchangeRateApiResponse.ExchangeRateData data,
                                         CurrencyPair currencyPair, DataProvider provider) {
        try {
            // Parse timestamp
            LocalDateTime rateTimestamp = LocalDateTime.parse(data.getCloseTime(),
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // Check if record already exists
            Optional<ExchangeRate> existing = exchangeRateRepository
                    .findByPairAndTimestamp(currencyPair.getId(), rateTimestamp.atZone(ZoneOffset.UTC).toInstant());

            ExchangeRate exchangeRate;
            if (existing.isPresent()) {
                exchangeRate = existing.get();
                exchangeRate.setUpdatedAt(Instant.now());
                logger.debug("Updating existing rate record for timestamp: {}", rateTimestamp);
            } else {
                exchangeRate = new ExchangeRate();
                exchangeRate.setCurrencyPair(currencyPair);
                exchangeRate.setProvider(provider);
                exchangeRate.setRateTimestamp(Instant.now());
            }

            // Map API response to entity fields
            BigDecimal bidAvg = new BigDecimal(data.getAverageBid());
            BigDecimal askAvg = new BigDecimal(data.getAverageAsk());
            BigDecimal bidHigh = new BigDecimal(data.getHighBid());
            BigDecimal askHigh = new BigDecimal(data.getHighAsk());
            BigDecimal bidLow = new BigDecimal(data.getLowBid());
            BigDecimal askLow = new BigDecimal(data.getLowAsk());

            // Set bid prices (using average as open/close since API doesn't provide OHLC)
            exchangeRate.setBidOpen(bidAvg);
            exchangeRate.setBidHigh(bidHigh);
            exchangeRate.setBidLow(bidLow);
            exchangeRate.setBidClose(bidAvg);
            exchangeRate.setBidAverage(bidAvg);

            // Set ask prices
            exchangeRate.setAskOpen(askAvg);
            exchangeRate.setAskHigh(askHigh);
            exchangeRate.setAskLow(askLow);
            exchangeRate.setAskClose(askAvg);
            exchangeRate.setAskAverage(askAvg);

            // Calculate derived values
            BigDecimal midRate = bidAvg.add(askAvg).divide(BigDecimal.valueOf(2));
            BigDecimal spread = askAvg.subtract(bidAvg);

            exchangeRate.setMidRate(midRate);
            exchangeRate.setSpread(spread);

            // Save the exchange rate
            exchangeRateRepository.save(exchangeRate);

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
