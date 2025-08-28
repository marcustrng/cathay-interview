package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.api.CurrenciesApi;
import com.cathay.interview.tutqq.model.Currency;
import com.cathay.interview.tutqq.model.CurrencyCreateRequest;
import com.cathay.interview.tutqq.model.CurrencyUpdateRequest;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;
import com.cathay.inteview.tutqq.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CurrencyController implements CurrenciesApi {

    private final CurrencyService currencyService;


    @Override
    public ResponseEntity<Currency> createCurrency(CurrencyCreateRequest currencyCreateRequest) {
        return ResponseEntity.status(201).body(currencyService.createCurrency(currencyCreateRequest));
    }

    @Override
    public ResponseEntity<Void> deleteCurrency(String code) {
        currencyService.deleteCurrency(code);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Currency> getCurrency(String code) {
        return currencyService.getCurrencyByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ListCurrencies200Response> listCurrencies(Boolean isActive) {
        return ResponseEntity.ok(new ListCurrencies200Response().data(currencyService.getAllCurrencies(isActive)));
    }

    @Override
    public ResponseEntity<Currency> updateCurrency(String code, CurrencyUpdateRequest currencyUpdateRequest) {
        return ResponseEntity.ok(currencyService.updateCurrency(code, currencyUpdateRequest));
    }
}
