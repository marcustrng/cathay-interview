package com.cathay.inteview.tutqq.service.impl;

import com.cathay.inteview.tutqq.exception.ExchangeRateApiException;
import com.cathay.inteview.tutqq.service.ApiRetryService.ApiCall;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApiRetryServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ApiRetryServiceImplTest {
    @Autowired
    private ApiRetryServiceImpl apiRetryServiceImpl;

    /**
     * Test {@link ApiRetryServiceImpl#executeWithRetry(ApiCall, String)}.
     *
     * <ul>
     *   <li>Given {@code Call}.
     *   <li>When {@link ApiCall} {@link ApiCall#call()} return {@code Call}.
     *   <li>Then return {@code Call}.
     * </ul>
     *
     * <p>Method under test: {@link ApiRetryServiceImpl#executeWithRetry(ApiCall, String)}
     */
    @Test
    @DisplayName(
            "Test executeWithRetry(ApiCall, String); given 'Call'; when ApiCall call() return 'Call'; then return 'Call'")
    void testExecuteWithRetry_givenCall_whenApiCallCallReturnCall_thenReturnCall() throws Exception {
        // Arrange
        ApiCall<Object> apiCall = mock(ApiCall.class);
        when(apiCall.call()).thenReturn("Call");

        // Act
        Object actualExecuteWithRetryResult =
                apiRetryServiceImpl.executeWithRetry(apiCall, "Operation");

        // Assert
        verify(apiCall).call();
        assertEquals("Call", actualExecuteWithRetryResult);
    }

    /**
     * Test {@link ApiRetryServiceImpl#executeWithRetry(ApiCall, String)}.
     *
     * <ul>
     *   <li>Then throw {@link ExchangeRateApiException}.
     * </ul>
     *
     * <p>Method under test: {@link ApiRetryServiceImpl#executeWithRetry(ApiCall, String)}
     */
    @Test
    @DisplayName("Test executeWithRetry(ApiCall, String); then throw ExchangeRateApiException")
    void testExecuteWithRetry_thenThrowExchangeRateApiException() throws Exception {
        // Arrange
        ApiCall<Object> apiCall = mock(ApiCall.class);
        when(apiCall.call()).thenThrow(new RestClientException("Executing {} - Attempt {}/{}"));

        // Act and Assert
        assertThrows(
                ExchangeRateApiException.class,
                () -> apiRetryServiceImpl.executeWithRetry(apiCall, "Operation"));
        verify(apiCall, atLeast(1)).call();
    }
}
