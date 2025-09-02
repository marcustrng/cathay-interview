package com.cathay.inteview.tutqq.service.provider;

import com.cathay.inteview.tutqq.client.OandaClient;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.constants.ExchangeRateProviderName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OandaExchangeRateProvider implements ExchangeRateProvider {

    private final OandaClient oandaClient;

    @Override
    public ExchangeRateProviderName getProviderName() {
        return ExchangeRateProviderName.OANDA;
    }

    @Override
    public String getApiBaseUrl() {
        return oandaClient.getApiBaseUrl();
    }

    @Override
    public ExchangeRateApiResponse getExchangeRates(String baseCurrency, String quoteCurrency, LocalDate startDate, LocalDate endDate) {
        return oandaClient.getExchangeRates(baseCurrency, quoteCurrency, startDate.toString(), endDate.toString());
    }
}
