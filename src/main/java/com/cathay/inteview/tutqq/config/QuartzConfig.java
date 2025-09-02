package com.cathay.inteview.tutqq.config;

import com.cathay.inteview.tutqq.property.ExchangeRateSyncProperties;
import com.cathay.inteview.tutqq.scheduler.ExchangeRateSyncJob;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final ExchangeRateSyncProperties syncProperties;

    @Bean
    public JobDetail exchangeRateSyncJobDetail() {
        return JobBuilder.newJob(ExchangeRateSyncJob.class)
                .withIdentity("exchangeRateSyncJob")
                .withDescription("Job to sync exchange rates from external API")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger exchangeRateSyncTrigger() {
        if (!syncProperties.isEnabled()) {
            return null; // disable trigger entirely if not enabled
        }

        return TriggerBuilder.newTrigger()
                .forJob(exchangeRateSyncJobDetail())
                .withIdentity("exchangeRateSyncTrigger")
                .withDescription("Trigger for exchange rate sync job")
                .startNow() // run immediately after app starts
                .withSchedule(CronScheduleBuilder.cronSchedule(syncProperties.getCron()))
                .build();
    }
}
