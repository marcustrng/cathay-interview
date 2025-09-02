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

//    private List<String> fetchActiveCurrencyCodes() {
//        try {
//            return currencyService.listCurrencies(true, 0, Integer.MAX_VALUE, null)
//                    .getContent()
//                    .stream()
//                    .filter(Objects::nonNull)
//                    .map(CurrencyDto::getCode)
//                    .toList();
//        } catch (Exception e) {
//            String message = "Failed to fetch active currency codes from CurrencyService";
//            log.error("{}: {}", message, e.getMessage(), e);
//            throw new IllegalStateException(message, e);
//        }
//    }
//
//    private List<String[]> generateCurrencyPairs(List<String> currencyCodes) {
//        if (currencyCodes.isEmpty()) {
//            log.warn("No active currencies found, skipping sync job");
//            return List.of();
//        }
//
//        return currencyCodes.stream()
//                .flatMap(base -> currencyCodes.stream()
//                        .filter(quote -> !quote.equals(base))
//                        .map(quote -> new String[]{base, quote}))
//                .toList();
//    }
//
//    private void syncCurrencyPairs(List<String[]> currencyPairs, LocalDate startDate, LocalDate endDate) {
//        for (String[] pair : currencyPairs) {
//            String base = pair[0];
//            String quote = pair[1];
//
//            try {
//                exchangeRateService.syncExchangeRates(base, quote, startDate, endDate);
//                delayBetweenApiCalls();
//            } catch (Exception e) {
//                // Log and continue, don’t break the whole job
//                log.error("Error syncing currency pair {}/{} between {} and {}: {}",
//                        base, quote, startDate, endDate, e.getMessage(), e);
//            }
//        }
//    }
//
//    private void delayBetweenApiCalls() {
//        try {
//            Thread.sleep(API_CALL_DELAY_MS);
//        } catch (InterruptedException ie) {
//            Thread.currentThread().interrupt();
//            log.warn("Exchange rate sync job interrupted during API delay", ie);
//        }
//    }
}
