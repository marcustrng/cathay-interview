package com.cathay.inteview.tutqq.service;

import com.cathay.inteview.tutqq.exception.ExchangeRateApiException;
import com.cathay.inteview.tutqq.service.impl.ApiRetryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;

class ApiRetryServiceTest {

    private ApiRetryService retryService;

    @BeforeEach
    void setUp() {
        retryService = new ApiRetryServiceImpl();
    }

    @Test
    void testExecuteWithRetry_successOnFirstTry() throws Exception {
        ApiRetryService.ApiCall<String> apiCall = () -> "success";

        String result = retryService.executeWithRetry(apiCall, "operation1");

        assertThat(result).isEqualTo("success");
    }

    @Test
    void testExecuteWithRetry_retriesOnRestClientExceptionAndSucceeds() {
        final int[] attempts = {0};

        ApiRetryService.ApiCall<String> apiCall = () -> {
            attempts[0]++;
            if (attempts[0] < 2) {
                throw new RestClientException("temporary failure");
            }
            return "ok";
        };

        String result = retryService.executeWithRetry(apiCall, "operation2");

        assertThat(result).isEqualTo("ok");
        assertThat(attempts[0]).isEqualTo(2); // retried once
    }

    @Test
    void testExecuteWithRetry_failsAfterMaxRetries() {
        ApiRetryService.ApiCall<String> apiCall = () -> {
            throw new RestClientException("always fails");
        };

        ExchangeRateApiException ex = assertThrows(ExchangeRateApiException.class,
                () -> retryService.executeWithRetry(apiCall, "operation3"));

        assertThat(ex.getMessage()).contains("API call failed after 3 attempts");
        assertThat(ex.getCause()).isInstanceOf(RestClientException.class);
    }

    @Test
    void testExecuteWithRetry_throwsRuntimeExceptionOnOtherException() {
        ApiRetryService.ApiCall<String> apiCall = () -> {
            throw new IllegalStateException("illegal state");
        };

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> retryService.executeWithRetry(apiCall, "operation4"));

        assertThat(ex.getCause()).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("illegal state");
    }

    @Test
    void testExecuteWithRetry_interruptedDuringRetry() {
        ApiRetryService.ApiCall<String> apiCall = () -> {
            throw new RestClientException("fail");
        };

        Thread testThread = new Thread(() -> {
            ExchangeRateApiException ex = assertThrows(ExchangeRateApiException.class,
                    () -> retryService.executeWithRetry(apiCall, "operation5"));
            assertThat(ex.getMessage()).contains("Retry interrupted");
        });

        testThread.start();
        testThread.interrupt();
    }
}
