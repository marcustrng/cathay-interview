package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateService {

    List<ExchangeRateDto> getExchangeRates(
            String baseCurrency,
            String quoteCurrency,
            LocalDate startDate,
            LocalDate endDate
    ) throws CurrencyPairNotFoundException, DateRangeExceededException;

    void validateDateRange(LocalDate startDate, LocalDate endDate) throws DateRangeExceededException;

    void validateCurrencyPair(String baseCurrency, String quoteCurrency) throws CurrencyPairNotFoundException;

    void syncExchangeRates(String baseCurrency, String quoteCurrency, LocalDate startDate, LocalDate endDate);
}
