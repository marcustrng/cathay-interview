package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.model.ExchangeRateResponse;
import com.cathay.interview.tutqq.model.HealthCheck200Response;
import com.cathay.interview.tutqq.model.ManualSync200Response;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ExchangeRateController.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class ExchangeRateControllerTest {
    @Autowired
    private ExchangeRateController exchangeRateController;

    @MockBean
    private ExchangeRateService exchangeRateService;

    /**
     * Test {@link ExchangeRateController#getExchangeRates(String, String, LocalDate, LocalDate)}.
     *
     * <p>Method under test: {@link ExchangeRateController#getExchangeRates(String, String, LocalDate,
     * LocalDate)}
     */
    @Test
    @DisplayName("Test getExchangeRates(String, String, LocalDate, LocalDate)")
    void testGetExchangeRates() throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        when(exchangeRateService.getExchangeRates(
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<LocalDate>any(),
                Mockito.<LocalDate>any(),
                Mockito.<Pageable>any()))
                .thenReturn(exchangeRateResponse);
        LocalDate startDate = LocalDate.of(1970, 1, 1);

        // Act
        ResponseEntity<ExchangeRateResponse> actualExchangeRates =
                exchangeRateController.getExchangeRates(
                        "Base", "Quote", startDate, LocalDate.of(1970, 1, 1));

        // Assert
        verify(exchangeRateService)
                .getExchangeRates(
                        eq("Base"),
                        eq("Quote"),
                        isA(LocalDate.class),
                        isA(LocalDate.class),
                        isA(Pageable.class));
        HttpStatusCode statusCode = actualExchangeRates.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertEquals(200, actualExchangeRates.getStatusCodeValue());
        assertEquals(HttpStatus.OK, statusCode);
        assertTrue(actualExchangeRates.hasBody());
        assertTrue(actualExchangeRates.getHeaders().isEmpty());
        assertSame(exchangeRateResponse, actualExchangeRates.getBody());
    }

    /**
     * Test {@link ExchangeRateController#healthCheck()}.
     *
     * <p>Method under test: {@link ExchangeRateController#healthCheck()}
     */
    @Test
    @DisplayName("Test healthCheck()")
    void testHealthCheck() {
        // Arrange and Act
        ResponseEntity<HealthCheck200Response> actualHealthCheckResult =
                exchangeRateController.healthCheck();

        // Assert
        HttpStatusCode statusCode = actualHealthCheckResult.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        HealthCheck200Response body = actualHealthCheckResult.getBody();
        assertEquals("ExchangeRatesApi", body.getService());
        assertEquals("healthy", body.getStatus());
        assertEquals(200, actualHealthCheckResult.getStatusCodeValue());
        assertEquals(HttpStatus.OK, statusCode);
        assertTrue(actualHealthCheckResult.hasBody());
        assertTrue(actualHealthCheckResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link ExchangeRateController#manualSync(String, String, String, String)}.
     *
     * <ul>
     *   <li>Then return Body Period is {@code 2020-03-01 to 2020-03-01}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateController#manualSync(String, String, String, String)}
     */
    @Test
    @DisplayName(
            "Test manualSync(String, String, String, String); then return Body Period is '2020-03-01 to 2020-03-01'")
    void testManualSync_thenReturnBodyPeriodIs20200301To20200301() {
        // Arrange
        doNothing()
                .when(exchangeRateService)
                .syncExchangeRates(
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any());

        // Act
        ResponseEntity<ManualSync200Response> actualManualSyncResult =
                exchangeRateController.manualSync("GBP", "GBP", "2020-03-01", "2020-03-01");

        // Assert
        verify(exchangeRateService).syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01");
        HttpStatusCode statusCode = actualManualSyncResult.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        ManualSync200Response body = actualManualSyncResult.getBody();
        assertEquals("2020-03-01 to 2020-03-01", body.getPeriod());
        assertEquals("Exchange rates synced successfully", body.getMessage());
        assertEquals("GBP/GBP", body.getPair());
        assertEquals("success", body.getStatus());
        assertEquals(200, actualManualSyncResult.getStatusCodeValue());
        assertEquals(HttpStatus.OK, statusCode);
    }

    /**
     * Test {@link ExchangeRateController#manualSync(String, String, String, String)}.
     *
     * <ul>
     *   <li>Then return Body Status is {@code error}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateController#manualSync(String, String, String, String)}
     */
    @Test
    @DisplayName(
            "Test manualSync(String, String, String, String); then return Body Status is 'error'")
    void testManualSync_thenReturnBodyStatusIsError() {
        // Arrange
        doThrow(new RuntimeException())
                .when(exchangeRateService)
                .syncExchangeRates(
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any());

        // Act
        ResponseEntity<ManualSync200Response> actualManualSyncResult =
                exchangeRateController.manualSync("GBP", "GBP", "2020-03-01", "2020-03-01");

        // Assert
        verify(exchangeRateService).syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01");
        HttpStatusCode statusCode = actualManualSyncResult.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        ManualSync200Response body = actualManualSyncResult.getBody();
        assertEquals("error", body.getStatus());
        assertNull(body.getMessage());
        assertNull(body.getPair());
        assertNull(body.getPeriod());
        assertEquals(500, actualManualSyncResult.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
    }
}
