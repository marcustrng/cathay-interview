package com.cathay.inteview.tutqq.service.provider;

import com.cathay.inteview.tutqq.constants.ExchangeRateProviderName;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ExchangeRateProviderFactory {

    private final Map<ExchangeRateProviderName, ExchangeRateProvider> providers;

    public ExchangeRateProviderFactory(List<ExchangeRateProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        ExchangeRateProvider::getProviderName,
                        Function.identity()
                ));
    }

    public ExchangeRateProvider getProvider(ExchangeRateProviderName name) {
        ExchangeRateProvider provider = providers.get(name);
        if (provider == null) {
            throw new IllegalArgumentException("No provider found for: " + name);
        }
        return provider;
    }
}
