package com.cathay.inteview.tutqq.service;

import java.time.LocalDateTime;

public interface MetricsService {
    void recordSyncStart();
    void recordSyncSuccess(long recordsProcessed);
    void recordSyncFailure(String error);
    long getTotalSyncJobs();
    long getSuccessfulSyncs();
    long getFailedSyncs();
    long getTotalRecordsProcessed();
    LocalDateTime getLastSyncTime();
    double getSuccessRate();
}
