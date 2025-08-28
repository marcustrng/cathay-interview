package com.cathay.inteview.tutqq.service;

public interface ApiRetryService {

    <T> T executeWithRetry(ApiCall<T> apiCall, String operation);

    @FunctionalInterface
    public interface ApiCall<T> {
        T call() throws Exception;
    }
}
