package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.GetExchangeRates200Response;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExchangeRateService {

    /**
     * Retrieves exchange rates for a currency pair within a date range
     */
    GetExchangeRates200Response getExchangeRates(
            String baseCurrency,
            String quoteCurrency,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) throws CurrencyPairNotFoundException, DateRangeExceededException;

    /**
     * Validates if the date range is within allowed limits (6 months)
     */
    void validateDateRange(LocalDate startDate, LocalDate endDate) throws DateRangeExceededException;

    /**
     * Validates if the currency pair exists and is active
     */
    void validateCurrencyPair(String baseCurrency, String quoteCurrency) throws CurrencyPairNotFoundException;

    void syncExchangeRates(String baseCurrency, String quoteCurrency, LocalDate startDate, LocalDate endDate);
}
