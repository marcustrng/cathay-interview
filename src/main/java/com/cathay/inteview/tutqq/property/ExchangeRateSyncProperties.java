package com.cathay.inteview.tutqq.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.exchange-rate.sync")
public class ExchangeRateSyncProperties {

    private boolean enabled = false;
    private int defaultDaysBack = 1;
    private int batchSize = 100;
}
