package com.cathay.inteview.tutqq.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ContextConfiguration(classes = {MetricsServiceImpl.class})
@ExtendWith(SpringExtension.class)
class MetricsServiceImplTest {
    @Autowired
    private MetricsServiceImpl metricsServiceImpl;

    /**
     * Test {@link MetricsServiceImpl#getFailedSyncs()}.
     *
     * <p>Method under test: {@link MetricsServiceImpl#getFailedSyncs()}
     */
    @Test
    @DisplayName("Test getFailedSyncs()")
    void testGetFailedSyncs() {
        // Arrange, Act and Assert
        assertEquals(0L, metricsServiceImpl.getFailedSyncs());
    }

    /**
     * Test {@link MetricsServiceImpl#getLastSyncTime()}.
     *
     * <p>Method under test: {@link MetricsServiceImpl#getLastSyncTime()}
     */
    @Test
    @DisplayName("Test getLastSyncTime()")
    void testGetLastSyncTime() {
        // Arrange, Act and Assert
        assertNull(metricsServiceImpl.getLastSyncTime());
    }
}
