package com.cathay.inteview.tutqq.service.impl;

import com.cathay.inteview.tutqq.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class MetricsServiceImpl implements MetricsService {

    private final AtomicLong totalSyncJobs = new AtomicLong(0);
    private final AtomicLong successfulSyncs = new AtomicLong(0);
    private final AtomicLong failedSyncs = new AtomicLong(0);
    private final AtomicLong totalRecordsProcessed = new AtomicLong(0);
    private final AtomicReference<LocalDateTime> lastSyncTime = new AtomicReference<>();


    @Override
    public void recordSyncStart() {
        totalSyncJobs.incrementAndGet();
        lastSyncTime.set(LocalDateTime.now());
    }

    @Override
    public void recordSyncSuccess(long recordsProcessed) {
        successfulSyncs.incrementAndGet();
        totalRecordsProcessed.addAndGet(recordsProcessed);
        log.info("Sync completed successfully. Processed {} records", recordsProcessed);
    }

    @Override
    public void recordSyncFailure(String error) {
        failedSyncs.incrementAndGet();
        log.error("Sync failed: {}", error);
    }

    @Override
    public long getTotalSyncJobs() { return totalSyncJobs.get(); }

    @Override
    public long getSuccessfulSyncs() { return successfulSyncs.get(); }

    @Override
    public long getFailedSyncs() { return failedSyncs.get(); }

    @Override
    public long getTotalRecordsProcessed() { return totalRecordsProcessed.get(); }

    @Override
    public LocalDateTime getLastSyncTime() { return lastSyncTime.get(); }

    @Override
    public double getSuccessRate() {
        long total = totalSyncJobs.get();
        return total > 0 ? (double) successfulSyncs.get() / total * 100 : 0;
    }
}
