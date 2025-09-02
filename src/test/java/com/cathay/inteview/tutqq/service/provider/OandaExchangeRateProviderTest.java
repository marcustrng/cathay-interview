package com.cathay.inteview.tutqq.service.provider;

import com.cathay.inteview.tutqq.client.OandaClient;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.constants.ExchangeRateProviderName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OandaExchangeRateProviderTest {

    private OandaClient oandaClient;
    private OandaExchangeRateProvider provider;

    @BeforeEach
    void setUp() {
        oandaClient = mock(OandaClient.class);
        provider = new OandaExchangeRateProvider(oandaClient);
    }

    @Test
    void testGetProviderName_returnsOanda() {
        assertThat(provider.getProviderName()).isEqualTo(ExchangeRateProviderName.OANDA);
    }

    @Test
    void testGetApiBaseUrl_delegatesToClient() {
        when(oandaClient.getApiBaseUrl()).thenReturn("https://api.oanda.com");

        String url = provider.getApiBaseUrl();

        assertThat(url).isEqualTo("https://api.oanda.com");
        verify(oandaClient, times(1)).getApiBaseUrl();
    }

    @Test
    void testGetExchangeRates_delegatesToClient() {
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 2);

        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse();
        when(oandaClient.getExchangeRates("USD", "EUR", "2025-09-01", "2025-09-02"))
                .thenReturn(mockResponse);

        ExchangeRateApiResponse response = provider.getExchangeRates("USD", "EUR", start, end);

        assertThat(response).isEqualTo(mockResponse);
        verify(oandaClient, times(1))
                .getExchangeRates("USD", "EUR", "2025-09-01", "2025-09-02");
    }
}

