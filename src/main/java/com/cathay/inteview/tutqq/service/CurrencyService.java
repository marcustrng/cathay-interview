package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    ListCurrencies200Response listCurrencies(Boolean isActive, Integer page, Integer size, List<String> sort);

    Optional<CurrencyDto> getCurrencyByCode(String code);

    String createCurrency(CurrencyDto currencyCreateRequest);

    CurrencyDto updateCurrency(String code, CurrencyDto currency);

    void deleteCurrency(String code);
}
