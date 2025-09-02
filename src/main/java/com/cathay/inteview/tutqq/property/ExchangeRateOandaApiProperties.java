package com.cathay.inteview.tutqq.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.exchange-rate.oanda-api")
public class ExchangeRateOandaApiProperties {

    /**
     * Base URL for OANDA Exchange Rates API.
     */
    private String baseUrl;

    /**
     * Timeout in milliseconds for API requests.
     */
    private int timeout;

    /**
     * Maximum number of retries for failed API calls.
     */
    private int maxRetries;
}
