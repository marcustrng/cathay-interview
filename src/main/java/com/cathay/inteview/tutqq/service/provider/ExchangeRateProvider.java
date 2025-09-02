package com.cathay.inteview.tutqq.service.provider;

import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.constants.ExchangeRateProviderName;

import java.time.LocalDate;

public interface ExchangeRateProvider {

    /**
     * Returns a unique provider name.
     * Use constants or enum to avoid magic strings.
     */
    ExchangeRateProviderName getProviderName();

    /**
     * Returns the API base URL for this provider.
     */
    String getApiBaseUrl();

    /**
     * Fetches exchange rates from the provider API.
     *
     * @param baseCurrency Base currency code, e.g., "EUR"
     * @param quoteCurrency Quote currency code, e.g., "USD"
     * @param startDate Start date for rates
     * @param endDate End date for rates
     * @return ExchangeRateApiResponse containing fetched rates
     */
    ExchangeRateApiResponse getExchangeRates(
            String baseCurrency,
            String quoteCurrency,
            LocalDate startDate,
            LocalDate endDate
    );
}
