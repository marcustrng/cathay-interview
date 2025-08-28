package com.cathay.inteview.tutqq.service.impl;

import com.cathay.inteview.tutqq.exception.ExchangeRateApiException;
import com.cathay.inteview.tutqq.service.ApiRetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class ApiRetryServiceImpl implements ApiRetryService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Override
    public <T> T executeWithRetry(ApiCall<T> apiCall, String operation) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.debug("Executing {} - Attempt {}/{}", operation, attempt, MAX_RETRIES);
                return apiCall.call();

            } catch (RestClientException e) {
                lastException = e;
                log.warn("API call failed for {} - Attempt {}/{}: {}",
                        operation, attempt, MAX_RETRIES, e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ExchangeRateApiException("Retry interrupted", ie);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        throw new ExchangeRateApiException(
                String.format("API call failed after %d attempts for %s", MAX_RETRIES, operation),
                lastException
        );
    }

}
