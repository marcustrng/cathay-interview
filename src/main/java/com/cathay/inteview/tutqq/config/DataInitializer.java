package com.cathay.inteview.tutqq.config;

import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.repository.CurrencyPairRepository;
import com.cathay.inteview.tutqq.repository.CurrencyRepository;
import com.cathay.inteview.tutqq.repository.DataProviderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CurrencyRepository currencyRepository;

    private final CurrencyPairRepository currencyPairRepository;

    private final DataProviderRepository dataProviderRepository;

    @Override
    public void run(String... args) {
        logger.info("Initializing reference data...");

        initializeCurrencies();
        initializeCurrencyPairs();
        initializeDataProviders();

        logger.info("Reference data initialization completed");
    }

    private void initializeCurrencies() {
        if (currencyRepository.count() == 0) {
            logger.info("Initializing currencies...");

            Currency[] currencies = {
                    new Currency("USD", "United States Dollar", (short) 840),
                    new Currency("EUR", "Euro", (short) 978),
                    new Currency("GBP", "British Pound Sterling", (short) 826),
                    new Currency("JPY", "Japanese Yen", (short) 392),
                    new Currency("CHF", "Swiss Franc", (short) 756),
                    new Currency("CAD", "Canadian Dollar", (short) 124),
                    new Currency("AUD", "Australian Dollar", (short) 36),
                    new Currency("CNY", "Chinese Yuan", (short) 156)
            };

            for (Currency currency : currencies) {
                if (currency.getCode().equals("JPY")) {
                    currency.setMinorUnit((short) 0); // Yen has no minor units
                }
                currencyRepository.save(currency);
            }

            logger.info("Initialized {} currencies", currencies.length);
        }
    }

    private void initializeCurrencyPairs() {
        if (currencyPairRepository.count() == 0) {
            logger.info("Initializing currency pairs...");

            String[][] pairs = {
                    {"EUR", "USD"}, {"GBP", "USD"}, {"USD", "JPY"},
                    {"USD", "CHF"}, {"EUR", "GBP"}, {"AUD", "USD"},
                    {"USD", "CAD"}, {"USD", "CNY"}
            };

            for (String[] pair : pairs) {
                CurrencyPair currencyPair = new CurrencyPair(pair[0], pair[1]);
                currencyPairRepository.save(currencyPair);
            }

            logger.info("Initialized {} currency pairs", pairs.length);
        }
    }

    private void initializeDataProviders() {
        if (dataProviderRepository.count() == 0) {
            logger.info("Initializing data providers...");

            DataProvider provider = new DataProvider(
                    "Exchange Rates API",
                    "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies"
            );
            provider.setRateLimitPerHour(1000);
            dataProviderRepository.save(provider);

            logger.info("Initialized data providers");
        }
    }
}
