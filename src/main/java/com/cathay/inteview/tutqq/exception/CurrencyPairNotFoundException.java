package com.cathay.inteview.tutqq.exception;

public class CurrencyPairNotFoundException extends RuntimeException {
    private final String baseCurrency;
    private final String quoteCurrency;

    public CurrencyPairNotFoundException(String message, String baseCurrency, String quoteCurrency) {
        super(message);
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    public String getBaseCurrency() { return baseCurrency; }
    public String getQuoteCurrency() { return quoteCurrency; }
}
