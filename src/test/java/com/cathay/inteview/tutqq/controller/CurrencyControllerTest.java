package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.model.Currency;
import com.cathay.interview.tutqq.model.CurrencyCreateRequest;
import com.cathay.interview.tutqq.model.CurrencyUpdateRequest;
import com.cathay.inteview.tutqq.service.CurrencyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CurrencyController.class})
@ExtendWith(SpringExtension.class)
class CurrencyControllerTest {
    @Autowired
    private CurrencyController currencyController;

    @MockBean
    private CurrencyService currencyService;

    /**
     * Test {@link CurrencyController#createCurrency(CurrencyCreateRequest)}.
     *
     * <p>Method under test: {@link CurrencyController#createCurrency(CurrencyCreateRequest)}
     */
    @Test
    @DisplayName("Test createCurrency(CurrencyCreateRequest)")
    void testCreateCurrency() {
        // Arrange
        Currency currency = new Currency();
        when(currencyService.createCurrency(Mockito.<CurrencyCreateRequest>any())).thenReturn(currency);

        // Act
        ResponseEntity<Currency> actualCreateCurrencyResult =
                currencyController.createCurrency(new CurrencyCreateRequest());

        // Assert
        verify(currencyService).createCurrency(isA(CurrencyCreateRequest.class));
        HttpStatusCode statusCode = actualCreateCurrencyResult.getStatusCode();
        assertInstanceOf(HttpStatus.class, statusCode);
        assertEquals(201, actualCreateCurrencyResult.getStatusCodeValue());
        assertEquals(HttpStatus.CREATED, statusCode);
        assertTrue(actualCreateCurrencyResult.hasBody());
        assertTrue(actualCreateCurrencyResult.getHeaders().isEmpty());
        assertSame(currency, actualCreateCurrencyResult.getBody());
    }

    /**
     * Test {@link CurrencyController#deleteCurrency(String)}.
     *
     * <p>Method under test: {@link CurrencyController#deleteCurrency(String)}
     */
    @Test
    @DisplayName("Test deleteCurrency(String)")
    void testDeleteCurrency() {
        // Arrange
        doNothing().when(currencyService).deleteCurrency(Mockito.<String>any());

        // Act
        ResponseEntity<Void> actualDeleteCurrencyResult = currencyController.deleteCurrency("Code");

        // Assert
        verify(currencyService).deleteCurrency("Code");
        HttpStatusCode statusCode = actualDeleteCurrencyResult.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualDeleteCurrencyResult.getBody());
        assertEquals(204, actualDeleteCurrencyResult.getStatusCodeValue());
        assertEquals(HttpStatus.NO_CONTENT, statusCode);
        assertFalse(actualDeleteCurrencyResult.hasBody());
        assertTrue(actualDeleteCurrencyResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link CurrencyController#getCurrency(String)}.
     *
     * <p>Method under test: {@link CurrencyController#getCurrency(String)}
     */
    @Test
    void testGetCurrency() {
        // Arrange
        Currency currency = new Currency();
        Optional<Currency> ofResult = Optional.of(currency);
        when(currencyService.getCurrencyByCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        ResponseEntity<Currency> actualCurrency = currencyController.getCurrency("Code");

        // Assert
        verify(currencyService).getCurrencyByCode("Code");
        HttpStatusCode statusCode = actualCurrency.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertEquals(200, actualCurrency.getStatusCodeValue());
        assertEquals(HttpStatus.OK, statusCode);
        assertTrue(actualCurrency.hasBody());
        assertTrue(actualCurrency.getHeaders().isEmpty());
        assertSame(currency, actualCurrency.getBody());
    }

    /**
     * Test {@link CurrencyController#updateCurrency(String, CurrencyUpdateRequest)}.
     *
     * <p>Method under test: {@link CurrencyController#updateCurrency(String, CurrencyUpdateRequest)}
     */
    @Test
    @DisplayName("Test updateCurrency(String, CurrencyUpdateRequest)")
    void testUpdateCurrency() {
        // Arrange
        Currency currency = new Currency();
        when(currencyService.updateCurrency(
                Mockito.<String>any(), Mockito.<CurrencyUpdateRequest>any()))
                .thenReturn(currency);

        // Act
        ResponseEntity<Currency> actualUpdateCurrencyResult =
                currencyController.updateCurrency("Code", new CurrencyUpdateRequest());

        // Assert
        verify(currencyService).updateCurrency(eq("Code"), isA(CurrencyUpdateRequest.class));
        HttpStatusCode statusCode = actualUpdateCurrencyResult.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertEquals(200, actualUpdateCurrencyResult.getStatusCodeValue());
        assertEquals(HttpStatus.OK, statusCode);
        assertTrue(actualUpdateCurrencyResult.hasBody());
        assertTrue(actualUpdateCurrencyResult.getHeaders().isEmpty());
        assertSame(currency, actualUpdateCurrencyResult.getBody());
    }
}
