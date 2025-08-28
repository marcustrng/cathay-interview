package com.cathay.inteview.tutqq.scheduler;

import com.cathay.inteview.tutqq.property.ExchangeRateSyncProperties;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ExchangeRateSyncJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateSyncJob.class);

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateSyncProperties syncProperties;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Starting scheduled exchange rate sync job");

        try {
            // Get date range for sync (last 2 days)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(syncProperties.getDefaultDaysBack());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            // Define currency pairs to sync
            String[][] currencyPairs = {
                    {"EUR", "USD"},
                    {"GBP", "USD"},
                    {"USD", "JPY"},
                    {"USD", "CHF"},
                    {"EUR", "GBP"},
                    {"AUD", "USD"},
                    {"USD", "CAD"}
            };

            // Sync each currency pair
            for (String[] pair : currencyPairs) {
                try {
                    exchangeRateService.syncExchangeRates(pair[0], pair[1], startDateStr, endDateStr);

                    // Add delay between API calls to respect rate limits
                    Thread.sleep(1000);

                } catch (Exception e) {
                    logger.error("Error syncing {}/{}: {}", pair[0], pair[1], e.getMessage());
                }
            }

            logger.info("Completed scheduled exchange rate sync job");

        } catch (Exception e) {
            logger.error("Error in scheduled sync job: {}", e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }
}
