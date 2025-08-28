package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.api.ExchangeRatesApi;
import com.cathay.interview.tutqq.model.ExchangeRateResponse;
import com.cathay.interview.tutqq.model.HealthCheck200Response;
import com.cathay.interview.tutqq.model.ManualSync200Response;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class ExchangeRateController implements ExchangeRatesApi {

    private final ExchangeRateService exchangeRateService;

    @Override
    public ResponseEntity<ExchangeRateResponse> getExchangeRates(String base, String quote, LocalDate startDate, LocalDate endDate) {
        return ResponseEntity.ok(exchangeRateService.getExchangeRates(base, quote, startDate, endDate, Pageable.ofSize(20)));
    }

    @Override
    public ResponseEntity<HealthCheck200Response> healthCheck() {
        return ResponseEntity.ok(new HealthCheck200Response().status("healthy").service("ExchangeRatesApi").timestamp(OffsetDateTime.now()));
    }

    @Override
    public ResponseEntity<ManualSync200Response> manualSync(String baseCurrency, String quoteCurrency, String startDate, String endDate) {
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
