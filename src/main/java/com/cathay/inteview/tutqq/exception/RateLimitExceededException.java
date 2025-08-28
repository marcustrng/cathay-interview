package com.cathay.inteview.tutqq.exception;

public class RateLimitExceededException extends RuntimeException {
    private final int rateLimit;
    private final int retryAfterSeconds;

    public RateLimitExceededException(String message, int rateLimit, int retryAfterSeconds) {
        super(message);
        this.rateLimit = rateLimit;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getRateLimit() { return rateLimit; }
    public int getRetryAfterSeconds() { return retryAfterSeconds; }
}
