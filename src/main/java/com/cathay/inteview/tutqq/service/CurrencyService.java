package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.CurrencyDto;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    List<CurrencyDto> getAllCurrencies(Boolean isActive);

    Optional<CurrencyDto> getCurrencyByCode(String code);

    CurrencyDto createCurrency(CurrencyDto currencyCreateRequest);

    CurrencyDto updateCurrency(String code, CurrencyDto currency);

    void deleteCurrency(String code);
}
