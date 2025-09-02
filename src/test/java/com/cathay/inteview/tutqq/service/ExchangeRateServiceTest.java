package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;
import com.cathay.inteview.tutqq.mapper.ExchangeRateMapper;
import com.cathay.inteview.tutqq.property.ExchangeRateSyncProperties;
import com.cathay.inteview.tutqq.repository.CurrencyPairRepository;
import com.cathay.inteview.tutqq.repository.DataProviderRepository;
import com.cathay.inteview.tutqq.repository.ExchangeRateRepository;
import com.cathay.inteview.tutqq.service.impl.ExchangeRateServiceImpl;
import com.cathay.inteview.tutqq.service.provider.ExchangeRateProvider;
import com.cathay.inteview.tutqq.service.provider.ExchangeRateProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateServiceTest {

    private ExchangeRateRepository exchangeRateRepository;
    private CurrencyPairRepository currencyPairRepository;
    private ExchangeRateMapper exchangeRateMapper;
    private DataProviderRepository dataProviderRepository;
    private CurrencyService currencyService;
    private ExchangeRateProviderFactory providerFactory;

    private ExchangeRateServiceImpl service;

    @BeforeEach
    void setUp() {
        exchangeRateRepository = mock(ExchangeRateRepository.class);
        currencyPairRepository = mock(CurrencyPairRepository.class);
        exchangeRateMapper = mock(ExchangeRateMapper.class);
        dataProviderRepository = mock(DataProviderRepository.class);
        currencyService = mock(CurrencyService.class);
        ExchangeRateSyncProperties syncProperties = mock(ExchangeRateSyncProperties.class);
        providerFactory = mock(ExchangeRateProviderFactory.class);

        service = new ExchangeRateServiceImpl(
                exchangeRateRepository,
                currencyPairRepository,
                exchangeRateMapper,
                dataProviderRepository,
                currencyService,
                syncProperties,
                providerFactory
        );
    }

    // ===================== getExchangeRates =====================
    @Test
    void testGetExchangeRates_success() throws Exception {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        CurrencyPair pair = new CurrencyPair(new Currency("USD"), new Currency("EUR"));
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode("USD", "EUR"))
                .thenReturn(Optional.of(pair));

        ExchangeRate rate = new ExchangeRate();
        when(exchangeRateRepository.findExchangeRatesByCurrencyPairAndDateRange(any(), any(), any(), any()))
                .thenReturn(List.of(rate));

        ExchangeRateDto dto = new ExchangeRateDto();
        when(exchangeRateMapper.toDtoList(List.of(rate))).thenReturn(List.of(dto));

        List<ExchangeRateDto> result = service.getExchangeRates("USD", "EUR", start, end);

        assertThat(result).containsExactly(dto);
        verify(exchangeRateRepository).findExchangeRatesByCurrencyPairAndDateRange(any(), any(), any(), any());
    }

    @Test
    void testGetExchangeRates_invalidDateRange_throws() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().minusDays(1);

        assertThatThrownBy(() -> service.getExchangeRates("USD", "EUR", start, end))
                .isInstanceOf(DateRangeExceededException.class);
    }

    @Test
    void testGetExchangeRates_invalidCurrencyPair_throws() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode("USD", "XXX"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getExchangeRates("USD", "XXX", start, end))
                .isInstanceOf(CurrencyPairNotFoundException.class);
    }

    // ===================== validateDateRange =====================
    @Test
    void testValidateDateRange_valid() throws Exception {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        service.validateDateRange(start, end); // no exception
    }

    @Test
    void testValidateDateRange_exceedsMaxDays_throws() {
        LocalDate start = LocalDate.now().minusDays(200);
        LocalDate end = LocalDate.now();

        assertThatThrownBy(() -> service.validateDateRange(start, end))
                .isInstanceOf(DateRangeExceededException.class)
                .hasMessageContaining("Maximum allowed date range");
    }

    // ===================== validateCurrencyPair =====================
    @Test
    void testValidateCurrencyPair_activePair() throws Exception {
        CurrencyPair pair = new CurrencyPair(new Currency("USD"), new Currency("EUR"));
        pair.setIsActive(true);

        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode("USD", "EUR"))
                .thenReturn(Optional.of(pair));

        service.validateCurrencyPair("USD", "EUR"); // no exception
    }

    @Test
    void testValidateCurrencyPair_inactivePair_throws() {
        CurrencyPair pair = new CurrencyPair(new Currency("USD"), new Currency("EUR"));
        pair.setIsActive(false);

        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode("USD", "EUR"))
                .thenReturn(Optional.of(pair));

        assertThatThrownBy(() -> service.validateCurrencyPair("USD", "EUR"))
                .isInstanceOf(CurrencyPairNotFoundException.class)
                .hasMessageContaining("currently inactive");
    }

    @Test
    void testValidateCurrencyPair_notFound_throws() {
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode("USD", "XXX"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateCurrencyPair("USD", "XXX"))
                .isInstanceOf(CurrencyPairNotFoundException.class)
                .hasMessageContaining("not supported");
    }

    // ===================== syncExchangeRates =====================
    @Test
    void testSyncExchangeRates_success() throws Exception {
        CurrencyDto usd = new CurrencyDto().code("USD");
        CurrencyDto eur = new CurrencyDto().code("EUR");

        when(currencyService.listCurrencies(true, 0, Integer.MAX_VALUE, null))
                .thenReturn(new ListCurrencies200Response().content(List.of(usd, eur)));

        ExchangeRateProvider provider = mock(ExchangeRateProvider.class);
        when(providerFactory.getProvider(any())).thenReturn(provider);

        ExchangeRateApiResponse.ExchangeRateData data = mock(ExchangeRateApiResponse.ExchangeRateData.class);
        ExchangeRateApiResponse response = mock(ExchangeRateApiResponse.class);
        when(response.getResponse()).thenReturn(List.of(data));
        when(provider.getExchangeRates(anyString(), anyString(), any(), any())).thenReturn(response);

        CurrencyPair pair = new CurrencyPair(new Currency("USD"), new Currency("EUR"));
        DataProvider dp = new DataProvider("Exchange Rates API", "url");
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(anyString(), anyString()))
                .thenReturn(Optional.of(pair));
        when(dataProviderRepository.findByName(anyString())).thenReturn(Optional.of(dp));

        ExchangeRate entity = new ExchangeRate();
        when(exchangeRateMapper.toEntity(data, pair, dp)).thenReturn(entity);

        service.syncExchangeRates();

        verify(exchangeRateRepository, atLeastOnce()).save(any(ExchangeRate.class));
        verify(dataProviderRepository, atLeastOnce()).save(any(DataProvider.class));

    }
}
