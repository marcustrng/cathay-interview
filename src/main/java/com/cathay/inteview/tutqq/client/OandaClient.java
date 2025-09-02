package com.cathay.inteview.tutqq.client;

import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.exception.OandaClientException;

public interface OandaClient {
    /**
     * Base URL for the OANDA API.
     */
    String getApiBaseUrl();

    /**
     * Retrieves exchange rates for the given currency pair and date range.
     *
     * @param baseCurrency  the base currency (e.g., "USD")
     * @param quoteCurrency the quote currency (e.g., "EUR")
     * @param startDate     start date in ISO format (yyyy-MM-dd)
     * @param endDate       end date in ISO format (yyyy-MM-dd)
     * @return exchange rate response payload
     * @throws OandaClientException if the request fails or response is invalid
     */
    ExchangeRateApiResponse getExchangeRates(
            String baseCurrency,
            String quoteCurrency,
            String startDate,
            String endDate
    ) throws OandaClientException;
}
