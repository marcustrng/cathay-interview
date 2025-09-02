package com.cathay.inteview.tutqq.service.provider;

import com.cathay.inteview.tutqq.constants.ExchangeRateProviderName;
import com.cathay.inteview.tutqq.exception.ProviderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExchangeRateProviderFactoryTest {

    private ExchangeRateProvider provider1;
    private ExchangeRateProvider provider2;
    private ExchangeRateProviderFactory factory;

    @BeforeEach
    void setUp() {
        // Mock providers
        provider1 = mock(ExchangeRateProvider.class);
        provider2 = mock(ExchangeRateProvider.class);

        when(provider1.getProviderName()).thenReturn(ExchangeRateProviderName.OANDA);
        when(provider2.getProviderName()).thenReturn(ExchangeRateProviderName.ANY_OTHER_PROVIDER);

        factory = new ExchangeRateProviderFactory(List.of(provider1, provider2));
    }

    @Test
    void testGetProvider_existingProvider_returnsProvider() {
        ExchangeRateProvider result = factory.getProvider(ExchangeRateProviderName.OANDA);
        assertThat(result).isEqualTo(provider1);

        ExchangeRateProvider result2 = factory.getProvider(ExchangeRateProviderName.ANY_OTHER_PROVIDER);
        assertThat(result2).isEqualTo(provider2);
    }

    @Test
    void testGetProvider_nonExistingProvider_throwsException() {
        assertThrows(ProviderNotFoundException.class, () -> {
            factory.getProvider(ExchangeRateProviderName.NON_EXISTENT);
        });
    }
}
