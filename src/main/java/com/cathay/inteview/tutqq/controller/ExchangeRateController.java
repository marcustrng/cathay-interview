package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.api.ExchangeRatesApi;
import com.cathay.interview.tutqq.model.GetExchangeRates200Response;
import com.cathay.interview.tutqq.model.ManualSync200Response;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class ExchangeRateController implements ExchangeRatesApi {

    private final ExchangeRateService exchangeRateService;

    @Override
    public ResponseEntity<GetExchangeRates200Response> getExchangeRates(String base, String quote, LocalDate startDate, LocalDate endDate) {
        return ResponseEntity.ok(exchangeRateService.getExchangeRates(base, quote, startDate, endDate, Pageable.ofSize(20)));
    }

    @Override
    public ResponseEntity<ManualSync200Response> manualSync(String baseCurrency, String quoteCurrency, LocalDate startDate, LocalDate endDate) {
        try {
            exchangeRateService.syncExchangeRates(baseCurrency, quoteCurrency, startDate, endDate);

            return ResponseEntity.ok(
                    new ManualSync200Response()
                            .status("success")
                            .message("Exchange rates synced successfully")
                            .pair(baseCurrency + "/" + quoteCurrency)
                            .period(startDate + " to " + endDate)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ManualSync200Response()
                            .status("error")
                            .message(e.getMessage())
            );
        }
    }
}
