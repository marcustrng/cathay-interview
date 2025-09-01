package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.api.CurrenciesApi;
import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;
import com.cathay.inteview.tutqq.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CurrencyController implements CurrenciesApi {

    private final CurrencyService currencyService;


    @Override
    public ResponseEntity<String> createCurrency(CurrencyDto body) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteCurrency(String code) {
        currencyService.deleteCurrency(code);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CurrencyDto> getCurrency(String code) {
        return currencyService.getCurrencyByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ListCurrencies200Response> listCurrencies(Boolean isActive, Integer page, Integer size, List<String> sort) {
        return null;
    }

    @Override
    public ResponseEntity<CurrencyDto> updateCurrency(String code, CurrencyDto currency) {
        return ResponseEntity.ok(currencyService.updateCurrency(code, currency));
    }
}
