package com.cathay.inteview.tutqq.scheduler;

import com.cathay.inteview.tutqq.property.ExchangeRateSyncProperties;
import com.cathay.inteview.tutqq.service.CurrencyService;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateSyncJob implements Job {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;
    private final ExchangeRateSyncProperties syncProperties;

    private static final int API_CALL_DELAY_MS = 1000;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting scheduled exchange rate sync job");

        try {
            exchangeRateService.syncExchangeRates();

            log.info("Completed scheduled exchange rate sync job");

        } catch (Exception e) {
            String message = "Fatal error during exchange rate sync job execution";
            log.error("{}: {}", message, e.getMessage(), e);
            throw new JobExecutionException(message, e);
        }
    }
}
