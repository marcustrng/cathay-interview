package com.cathay.inteview.tutqq.exception;

public class ProviderNotFoundException extends RuntimeException {

    private final String providerName;

    public ProviderNotFoundException(String providerName) {
        super(String.format("Exchange rate provider '%s' not found", providerName));
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }
}
