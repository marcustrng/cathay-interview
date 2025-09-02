package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.interview.tutqq.model.ManualSync200Response;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

class ExchangeRateControllerTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- getExchangeRates -------------------
    @Test
    void getExchangeRates_ShouldReturnOkWithData() throws Exception {
        ExchangeRateDto dto1 = new ExchangeRateDto();
        ExchangeRateDto dto2 = new ExchangeRateDto();
        List<ExchangeRateDto> rates = List.of(dto1, dto2);

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 2);

        when(exchangeRateService.getExchangeRates("EUR", "USD", startDate, endDate)).thenReturn(rates);

        ResponseEntity<List<ExchangeRateDto>> response = exchangeRateController.getExchangeRates("EUR", "USD", startDate, endDate);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(rates);
        verify(exchangeRateService, times(1)).getExchangeRates("EUR", "USD", startDate, endDate);
    }

    // ------------------- manualSync -------------------
    @Test
    void manualSync_ShouldReturnSuccess_WhenNoException() throws Exception {
        doNothing().when(exchangeRateService).syncExchangeRates();

        ResponseEntity<ManualSync200Response> response = exchangeRateController.manualSync();

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("success");
        assertThat(response.getBody().getMessage()).isEqualTo("Exchange rates synced successfully");

        verify(exchangeRateService, times(1)).syncExchangeRates();
    }

    @Test
    void manualSync_ShouldReturnError_WhenExceptionThrown() throws Exception {
        doThrow(new RuntimeException("API error")).when(exchangeRateService).syncExchangeRates();

        ResponseEntity<ManualSync200Response> response = exchangeRateController.manualSync();

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("error");
        assertThat(response.getBody().getMessage()).isEqualTo("API error");

        verify(exchangeRateService, times(1)).syncExchangeRates();
    }
}
