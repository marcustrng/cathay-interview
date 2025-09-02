package com.cathay.inteview.tutqq.scheduler;

import com.cathay.inteview.tutqq.property.ExchangeRateSyncProperties;
import com.cathay.inteview.tutqq.service.CurrencyService;
import com.cathay.inteview.tutqq.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ExchangeRateSyncJobTest {

    private ExchangeRateService exchangeRateService;
    private ExchangeRateSyncJob job;
    private JobExecutionContext context;

    @BeforeEach
    void setUp() {
        exchangeRateService = mock(ExchangeRateService.class);
        CurrencyService currencyService = mock(CurrencyService.class);
        ExchangeRateSyncProperties syncProperties = mock(ExchangeRateSyncProperties.class);
        context = mock(JobExecutionContext.class);

        job = new ExchangeRateSyncJob(exchangeRateService, currencyService, syncProperties);
    }

    @Test
    void testExecute_success() throws Exception {
        // Arrange
        doNothing().when(exchangeRateService).syncExchangeRates();

        // Act
        job.execute(context);

        // Assert
        verify(exchangeRateService, times(1)).syncExchangeRates();
    }

    @Test
    void testExecute_exceptionThrowsJobExecutionException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("API failure")).when(exchangeRateService).syncExchangeRates();

        // Act & Assert
        assertThatThrownBy(() -> job.execute(context))
                .isInstanceOf(JobExecutionException.class)
                .hasMessageContaining("Fatal error during exchange rate sync job execution");

        verify(exchangeRateService, times(1)).syncExchangeRates();
    }
}
