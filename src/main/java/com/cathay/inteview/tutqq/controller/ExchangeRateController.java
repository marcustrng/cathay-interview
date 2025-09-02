package com.cathay.inteview.tutqq.controller;

import com.cathay.interview.tutqq.api.ExchangeRatesApi;
import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.interview.tutqq.model.ManualSync200Response;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class ExchangeRateController implements ExchangeRatesApi {

    private final ExchangeRateService exchangeRateService;

    @Override
    public ResponseEntity<List<ExchangeRateDto>> getExchangeRates(String base, String quote, LocalDate startDate, LocalDate endDate) {
        return ResponseEntity.ok(exchangeRateService.getExchangeRates(base, quote, startDate, endDate));
    }

    @Override
    public ResponseEntity<ManualSync200Response> manualSync() {
        try {
            exchangeRateService.syncExchangeRates();

            return ResponseEntity.ok(
                    new ManualSync200Response()
                            .status("success")
                            .message("Exchange rates synced successfully")
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
