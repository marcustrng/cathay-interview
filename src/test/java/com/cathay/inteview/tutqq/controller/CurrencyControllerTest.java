package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;
import com.cathay.inteview.tutqq.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- createCurrency -------------------
    @Test
    void createCurrency_ShouldReturnCreatedResponse() {
        CurrencyDto dto = new CurrencyDto();
        dto.setCode("USD");
        when(currencyService.createCurrency(dto)).thenReturn("USD");

        ResponseEntity<String> response = currencyController.createCurrency(dto);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo("USD");
        assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/currencies/USD");

        verify(currencyService, times(1)).createCurrency(dto);
    }

    // ------------------- deleteCurrency -------------------
    @Test
    void deleteCurrency_ShouldReturnNoContent() {
        doNothing().when(currencyService).deleteCurrency("USD");

        ResponseEntity<Void> response = currencyController.deleteCurrency("USD");

        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        verify(currencyService, times(1)).deleteCurrency("USD");
    }

    // ------------------- getCurrency -------------------
    @Test
    void getCurrency_ShouldReturnOk_WhenCurrencyExists() {
        CurrencyDto dto = new CurrencyDto();
        dto.setCode("USD");
        when(currencyService.getCurrencyByCode("USD")).thenReturn(Optional.of(dto));

        ResponseEntity<CurrencyDto> response = currencyController.getCurrency("USD");

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(currencyService, times(1)).getCurrencyByCode("USD");
    }

    @Test
    void getCurrency_ShouldReturnNotFound_WhenCurrencyDoesNotExist() {
        when(currencyService.getCurrencyByCode("USD")).thenReturn(Optional.empty());

        ResponseEntity<CurrencyDto> response = currencyController.getCurrency("USD");

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(currencyService, times(1)).getCurrencyByCode("USD");
    }

    // ------------------- listCurrencies -------------------
    @Test
    void listCurrencies_ShouldReturnOk() {
        ListCurrencies200Response responseDto = new ListCurrencies200Response();
        when(currencyService.listCurrencies(true, 0, 20, null)).thenReturn(responseDto);

        ResponseEntity<ListCurrencies200Response> response =
                currencyController.listCurrencies(true, 0, 20, null);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
        verify(currencyService, times(1)).listCurrencies(true, 0, 20, null);
    }

    // ------------------- updateCurrency -------------------
    @Test
    void updateCurrency_ShouldReturnUpdatedCurrency() {
        CurrencyDto dto = new CurrencyDto();
        dto.setCode("USD");
        when(currencyService.updateCurrency("USD", dto)).thenReturn(dto);

        ResponseEntity<CurrencyDto> response = currencyController.updateCurrency("USD", dto);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(currencyService, times(1)).updateCurrency("USD", dto);
    }
}
