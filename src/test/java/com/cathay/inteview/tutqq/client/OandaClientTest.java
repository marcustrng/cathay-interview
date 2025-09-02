package com.cathay.inteview.tutqq.client;

import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.client.impl.OandaClientImpl;
import com.cathay.inteview.tutqq.property.ExchangeRateOandaApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OandaClientTest {

    private ExchangeRateOandaApiProperties apiProperties;
    private RestTemplate restTemplate;
    private OandaClientImpl oandaClient;

    @BeforeEach
    void setUp() {
        apiProperties = mock(ExchangeRateOandaApiProperties.class);
        restTemplate = mock(RestTemplate.class);
        oandaClient = new OandaClientImpl(apiProperties, restTemplate);
    }

    @Test
    void testGetApiBaseUrl_returnsBaseUrl() {
        // Arrange
        when(apiProperties.getBaseUrl()).thenReturn("https://example.com/api");

        // Act
        String baseUrl = oandaClient.getApiBaseUrl();

        // Assert
        assertThat(baseUrl).isEqualTo("https://example.com/api");
        verify(apiProperties, times(1)).getBaseUrl();
    }

    @Test
    void testGetExchangeRates_callsRestTemplateAndReturnsResponse() {
        // Arrange
        String baseCurrency = "USD";
        String quoteCurrency = "EUR";
        String startDate = "2025-01-01";
        String endDate = "2025-01-02";
        String apiUrl = "https://example.com/api?base=USD&quote=EUR&data_type=chart&start_date=2025-01-01&end_date=2025-01-02";

        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse();
        when(apiProperties.getBaseUrl()).thenReturn("https://example.com/api");
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class))).thenReturn(mockResponse);

        // Act
        ExchangeRateApiResponse response = oandaClient.getExchangeRates(baseCurrency, quoteCurrency, startDate, endDate);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response).isSameAs(mockResponse);

        // Verify correct URL was called
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate, times(1)).getForObject(urlCaptor.capture(), eq(ExchangeRateApiResponse.class));
        assertThat(urlCaptor.getValue()).isEqualTo(apiUrl);
    }
}
