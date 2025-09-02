package com.cathay.inteview.tutqq.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.exchange-rate.sync")
public class ExchangeRateSyncProperties {

    /**
     * Whether synchronization is enabled.
     */
    private boolean enabled;

    /**
     * Default number of days back to fetch exchange rates.
     */
    private int defaultDaysBack;

    /**
     * Batch size for syncing data.
     */
    private int batchSize;

    /**
     * Cron expression for scheduling the job
     * Default: run daily at 2 AM
     */
    private String cron = "0 0 2 * * ?";
}
