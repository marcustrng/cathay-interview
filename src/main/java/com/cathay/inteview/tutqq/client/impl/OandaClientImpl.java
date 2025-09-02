package com.cathay.inteview.tutqq.client.impl;

import com.cathay.inteview.tutqq.client.OandaClient;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.property.ExchangeRateOandaApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OandaClientImpl implements OandaClient {

    private final ExchangeRateOandaApiProperties apiProperties;
    private final RestTemplate restTemplate;

    @Override
    public String getApiBaseUrl() {
        return apiProperties.getBaseUrl();
    }

    @Override
    public ExchangeRateApiResponse getExchangeRates(String baseCurrency, String quoteCurrency, String startDate, String endDate) {
        // Build API URL
        String url = String.format("%s?base=%s&quote=%s&data_type=chart&start_date=%s&end_date=%s",
                getApiBaseUrl(), baseCurrency, quoteCurrency, startDate, endDate);

        // Call external API
        return restTemplate.getForObject(url, ExchangeRateApiResponse.class);
    }
}
