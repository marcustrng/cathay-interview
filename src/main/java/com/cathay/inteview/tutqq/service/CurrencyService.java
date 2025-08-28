package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.Currency;
import com.cathay.interview.tutqq.model.CurrencyCreateRequest;
import com.cathay.interview.tutqq.model.CurrencyUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    List<Currency> getAllCurrencies(Boolean isActive);

    Optional<Currency> getCurrencyByCode(String code);

    Currency createCurrency(CurrencyCreateRequest currencyCreateRequest);

    Currency updateCurrency(String code, CurrencyUpdateRequest currencyUpdateRequest);

    void deleteCurrency(String code);
}
