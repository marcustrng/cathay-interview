package com.cathay.inteview.tutqq.service.impl;

import com.cathay.inteview.tutqq.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.dto.ExchangeRateApiResponse.ExchangeRateData;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import com.cathay.inteview.tutqq.exception.CurrencyPairNotFoundException;
import com.cathay.inteview.tutqq.exception.DateRangeExceededException;
import com.cathay.inteview.tutqq.mapper.ExchangeRateMapper;
import com.cathay.inteview.tutqq.mapper.ExchangeRateMapperImpl;
import com.cathay.inteview.tutqq.repository.CurrencyPairRepository;
import com.cathay.inteview.tutqq.repository.DataProviderRepository;
import com.cathay.inteview.tutqq.repository.ExchangeRateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ExchangeRateServiceImpl.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class ExchangeRateServiceImplTest {
    @MockBean
    private CurrencyPairRepository currencyPairRepository;

    @MockBean
    private DataProviderRepository dataProviderRepository;

    @MockBean
    private ExchangeRateMapper exchangeRateMapper;

    @MockBean
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateServiceImpl exchangeRateServiceImpl;

    @MockBean
    private RestTemplate restTemplate;

    /**
     * Test {@link ExchangeRateServiceImpl#getExchangeRates(String, String, LocalDate, LocalDate,
     * Pageable)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#getExchangeRates(String, String,
     * LocalDate, LocalDate, Pageable)}
     */
    @Test
    @DisplayName("Test getExchangeRates(String, String, LocalDate, LocalDate, Pageable)")
    void testGetExchangeRates() throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));
        LocalDate startDate2 = LocalDate.of(1970, 1, 1);

        // Act and Assert
        assertThrows(
                DateRangeExceededException.class,
                () ->
                        exchangeRateServiceImpl.getExchangeRates(
                                "GBP", "GBP", startDate2, LocalDate.of(1970, 1, 1), null));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#getExchangeRates(String, String, LocalDate, LocalDate,
     * Pageable)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#getExchangeRates(String, String,
     * LocalDate, LocalDate, Pageable)}
     */
    @Test
    @DisplayName("Test getExchangeRates(String, String, LocalDate, LocalDate, Pageable)")
    void testGetExchangeRates2() throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        Optional<CurrencyPair> emptyResult = Optional.empty();
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);
        LocalDate startDate = LocalDate.of(1970, 1, 1);

        // Act and Assert
        assertThrows(
                CurrencyPairNotFoundException.class,
                () ->
                        exchangeRateServiceImpl.getExchangeRates(
                                "GBP", "GBP", startDate, LocalDate.of(1970, 1, 1), null));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#getExchangeRates(String, String, LocalDate, LocalDate,
     * Pageable)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#getExchangeRates(String, String,
     * LocalDate, LocalDate, Pageable)}
     */
    @Test
    @DisplayName("Test getExchangeRates(String, String, LocalDate, LocalDate, Pageable)")
    void testGetExchangeRates3() throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(exchangeRateRepository.findExchangeRatesByCurrencyPairAndDateRange(
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<Instant>any(),
                Mockito.<Instant>any(),
                Mockito.<Pageable>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));

        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(true);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        LocalDate startDate2 = LocalDate.of(1970, 1, 1);

        // Act and Assert
        assertThrows(
                DateRangeExceededException.class,
                () ->
                        exchangeRateServiceImpl.getExchangeRates(
                                "GBP", "GBP", startDate2, LocalDate.of(1970, 1, 1), null));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
        verify(exchangeRateRepository)
                .findExchangeRatesByCurrencyPairAndDateRange(
                        eq("GBP"), eq("GBP"), isA(Instant.class), isA(Instant.class), isNull());
    }

    /**
     * Test {@link ExchangeRateServiceImpl#getExchangeRates(String, String, LocalDate, LocalDate,
     * Pageable)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#getExchangeRates(String, String,
     * LocalDate, LocalDate, Pageable)}
     */
    @Test
    @DisplayName("Test getExchangeRates(String, String, LocalDate, LocalDate, Pageable)")
    void testGetExchangeRates4() throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        when(exchangeRateRepository.findExchangeRatesByCurrencyPairAndDateRange(
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<Instant>any(),
                Mockito.<Instant>any(),
                Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(true);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(exchangeRateMapper.toDtoList(Mockito.<List<ExchangeRate>>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));
        LocalDate startDate2 = LocalDate.of(1970, 1, 1);

        // Act and Assert
        assertThrows(
                DateRangeExceededException.class,
                () ->
                        exchangeRateServiceImpl.getExchangeRates(
                                "GBP", "GBP", startDate2, LocalDate.of(1970, 1, 1), null));
        verify(exchangeRateMapper).toDtoList(isA(List.class));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
        verify(exchangeRateRepository)
                .findExchangeRatesByCurrencyPairAndDateRange(
                        eq("GBP"), eq("GBP"), isA(Instant.class), isA(Instant.class), isNull());
    }

    /**
     * Test {@link ExchangeRateServiceImpl#getExchangeRates(String, String, LocalDate, LocalDate,
     * Pageable)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyPair#CurrencyPair()} IsActive is {@code false}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#getExchangeRates(String, String,
     * LocalDate, LocalDate, Pageable)}
     */
    @Test
    @DisplayName(
            "Test getExchangeRates(String, String, LocalDate, LocalDate, Pageable); given CurrencyPair() IsActive is 'false'")
    void testGetExchangeRates_givenCurrencyPairIsActiveIsFalse()
            throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(false);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        LocalDate startDate = LocalDate.of(1970, 1, 1);

        // Act and Assert
        assertThrows(
                CurrencyPairNotFoundException.class,
                () ->
                        exchangeRateServiceImpl.getExchangeRates(
                                "GBP", "GBP", startDate, LocalDate.of(1970, 1, 1), null));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#getExchangeRates(String, String, LocalDate, LocalDate,
     * Pageable)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyPairRepository}.
     *   <li>When ofYearDay four and four.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#getExchangeRates(String, String,
     * LocalDate, LocalDate, Pageable)}
     */
    @Test
    @DisplayName(
            "Test getExchangeRates(String, String, LocalDate, LocalDate, Pageable); given CurrencyPairRepository; when ofYearDay four and four")
    void testGetExchangeRates_givenCurrencyPairRepository_whenOfYearDayFourAndFour()
            throws CurrencyPairNotFoundException, DateRangeExceededException {
        // Arrange
        LocalDate startDate = LocalDate.ofYearDay(4, 4);

        // Act and Assert
        assertThrows(
                DateRangeExceededException.class,
                () ->
                        exchangeRateServiceImpl.getExchangeRates(
                                "GBP", "GBP", startDate, LocalDate.of(1970, 1, 1), null));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#validateDateRange(LocalDate, LocalDate)}.
     *
     * <ul>
     *   <li>When {@link LocalDate} with {@code 1970} and one and one.
     *   <li>Then does not throw.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#validateDateRange(LocalDate, LocalDate)}
     */
    @Test
    @DisplayName(
            "Test validateDateRange(LocalDate, LocalDate); when LocalDate with '1970' and one and one; then does not throw")
    void testValidateDateRange_whenLocalDateWith1970AndOneAndOne_thenDoesNotThrow()
            throws DateRangeExceededException {
        // Arrange
        LocalDate startDate = LocalDate.of(1970, 1, 1);

        // Act and Assert
        assertDoesNotThrow(
                () -> exchangeRateServiceImpl.validateDateRange(startDate, LocalDate.of(1970, 1, 1)));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}
     */
    @Test
    @DisplayName("Test validateCurrencyPair(String, String)")
    void testValidateCurrencyPair() throws CurrencyPairNotFoundException {
        // Arrange
        CurrencyPairRepository currencyPairRepository = mock(CurrencyPairRepository.class);
        Optional<CurrencyPair> emptyResult = Optional.empty();
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);
        ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);

        // Act and Assert
        assertThrows(
                CurrencyPairNotFoundException.class,
                () ->
                        new ExchangeRateServiceImpl(
                                exchangeRateRepository,
                                currencyPairRepository,
                                new ExchangeRateMapperImpl(),
                                mock(DataProviderRepository.class),
                                mock(RestTemplate.class))
                                .validateCurrencyPair("GBP", "GBP"));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyPair#CurrencyPair()} IsActive is {@code false}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}
     */
    @Test
    @DisplayName(
            "Test validateCurrencyPair(String, String); given CurrencyPair() IsActive is 'false'")
    void testValidateCurrencyPair_givenCurrencyPairIsActiveIsFalse()
            throws CurrencyPairNotFoundException {
        // Arrange
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(false);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        CurrencyPairRepository currencyPairRepository = mock(CurrencyPairRepository.class);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);

        // Act and Assert
        assertThrows(
                CurrencyPairNotFoundException.class,
                () ->
                        new ExchangeRateServiceImpl(
                                exchangeRateRepository,
                                currencyPairRepository,
                                new ExchangeRateMapperImpl(),
                                mock(DataProviderRepository.class),
                                mock(RestTemplate.class))
                                .validateCurrencyPair("GBP", "GBP"));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyPair#CurrencyPair()} IsActive is {@code true}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}
     */
    @Test
    @DisplayName("Test validateCurrencyPair(String, String); given CurrencyPair() IsActive is 'true'")
    void testValidateCurrencyPair_givenCurrencyPairIsActiveIsTrue()
            throws CurrencyPairNotFoundException {
        // Arrange
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(true);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        CurrencyPairRepository currencyPairRepository = mock(CurrencyPairRepository.class);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);

        // Act
        new ExchangeRateServiceImpl(
                exchangeRateRepository,
                currencyPairRepository,
                new ExchangeRateMapperImpl(),
                mock(DataProviderRepository.class),
                mock(RestTemplate.class))
                .validateCurrencyPair("GBP", "GBP");

        // Assert
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}.
     *
     * <ul>
     *   <li>Then throw {@link DateRangeExceededException}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#validateCurrencyPair(String, String)}
     */
    @Test
    @DisplayName("Test validateCurrencyPair(String, String); then throw DateRangeExceededException")
    void testValidateCurrencyPair_thenThrowDateRangeExceededException()
            throws CurrencyPairNotFoundException {
        // Arrange
        CurrencyPairRepository currencyPairRepository = mock(CurrencyPairRepository.class);
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 2, 3, 3));
        ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);

        // Act and Assert
        assertThrows(
                DateRangeExceededException.class,
                () ->
                        new ExchangeRateServiceImpl(
                                exchangeRateRepository,
                                currencyPairRepository,
                                new ExchangeRateMapperImpl(),
                                mock(DataProviderRepository.class),
                                mock(RestTemplate.class))
                                .validateCurrencyPair("GBP", "GBP"));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
    }

    /**
     * Test {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String, String)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String,
     * String)}
     */
    @Test
    @DisplayName("Test syncExchangeRates(String, String, String, String)")
    void testSyncExchangeRates() throws RestClientException {
        // Arrange
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setAverageAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setAverageBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setBaseCurrency("GBP");
        exchangeRateData.setCloseTime("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setQuoteCurrency("GBP");

        ArrayList<ExchangeRateData> response = new ArrayList<>();
        response.add(exchangeRateData);

        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setResponse(response);
        when(restTemplate.getForObject(
                Mockito.<String>any(),
                Mockito.<Class<ExchangeRateApiResponse>>any(),
                isA(Object[].class)))
                .thenReturn(exchangeRateApiResponse);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> exchangeRateServiceImpl.syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01"));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
        verify(restTemplate)
                .getForObject(
                        eq(
                                "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=GBP&quote=GBP&data_type=chart&start_date=2020-03-01&end_date=2020-03-01"),
                        isA(Class.class),
                        isA(Object[].class));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String, String)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String,
     * String)}
     */
    @Test
    @DisplayName("Test syncExchangeRates(String, String, String, String)")
    void testSyncExchangeRates2() throws RestClientException {
        // Arrange
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(true);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(dataProviderRepository.findByName(Mockito.<String>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setAverageAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setAverageBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setBaseCurrency("GBP");
        exchangeRateData.setCloseTime("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setQuoteCurrency("GBP");

        ArrayList<ExchangeRateData> response = new ArrayList<>();
        response.add(exchangeRateData);

        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setResponse(response);
        when(restTemplate.getForObject(
                Mockito.<String>any(),
                Mockito.<Class<ExchangeRateApiResponse>>any(),
                isA(Object[].class)))
                .thenReturn(exchangeRateApiResponse);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> exchangeRateServiceImpl.syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01"));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
        verify(dataProviderRepository).findByName("Exchange Rates API");
        verify(restTemplate)
                .getForObject(
                        eq(
                                "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=GBP&quote=GBP&data_type=chart&start_date=2020-03-01&end_date=2020-03-01"),
                        isA(Class.class),
                        isA(Object[].class));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String, String)}.
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String,
     * String)}
     */
    @Test
    @DisplayName("Test syncExchangeRates(String, String, String, String)")
    void testSyncExchangeRates3() throws RestClientException {
        // Arrange
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(restTemplate.getForObject(
                Mockito.<String>any(),
                Mockito.<Class<ExchangeRateApiResponse>>any(),
                isA(Object[].class)))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> exchangeRateServiceImpl.syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01"));
        verify(restTemplate)
                .getForObject(
                        eq(
                                "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=GBP&quote=GBP&data_type=chart&start_date=2020-03-01&end_date=2020-03-01"),
                        isA(Class.class),
                        isA(Object[].class));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String, String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyPairRepository}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String,
     * String)}
     */
    @Test
    @DisplayName(
            "Test syncExchangeRates(String, String, String, String); given CurrencyPairRepository")
    void testSyncExchangeRates_givenCurrencyPairRepository() throws RestClientException {
        // Arrange
        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setResponse(new ArrayList<>());
        when(restTemplate.getForObject(
                Mockito.<String>any(),
                Mockito.<Class<ExchangeRateApiResponse>>any(),
                isA(Object[].class)))
                .thenReturn(exchangeRateApiResponse);

        // Act
        exchangeRateServiceImpl.syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01");

        // Assert
        verify(restTemplate)
                .getForObject(
                        eq(
                                "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=GBP&quote=GBP&data_type=chart&start_date=2020-03-01&end_date=2020-03-01"),
                        isA(Class.class),
                        isA(Object[].class));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String, String)}.
     *
     * <ul>
     *   <li>Then calls {@link CurrencyPairRepository#save(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String,
     * String)}
     */
    @Test
    @DisplayName("Test syncExchangeRates(String, String, String, String); then calls save(Object)")
    void testSyncExchangeRates_thenCallsSave() throws RestClientException {
        // Arrange
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(true);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        CurrencyPairRepository currencyPairRepository = mock(CurrencyPairRepository.class);
        when(currencyPairRepository.save(Mockito.<CurrencyPair>any())).thenReturn(currencyPair);
        Optional<CurrencyPair> emptyResult = Optional.empty();
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);
        DataProviderRepository dataProviderRepository = mock(DataProviderRepository.class);
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        when(dataProviderRepository.findByName(Mockito.<String>any()))
                .thenThrow(
                        new DateRangeExceededException(
                                "An error occurred", startDate, LocalDate.of(1970, 1, 1), 4, 3, 3));

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setAverageAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setAverageBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setBaseCurrency("GBP");
        exchangeRateData.setCloseTime("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setQuoteCurrency("GBP");

        ArrayList<ExchangeRateData> response = new ArrayList<>();
        response.add(exchangeRateData);

        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setResponse(response);
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject(
                Mockito.<String>any(),
                Mockito.<Class<ExchangeRateApiResponse>>any(),
                isA(Object[].class)))
                .thenReturn(exchangeRateApiResponse);
        ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () ->
                        new ExchangeRateServiceImpl(
                                exchangeRateRepository,
                                currencyPairRepository,
                                new ExchangeRateMapperImpl(),
                                dataProviderRepository,
                                restTemplate)
                                .syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01"));
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
        verify(dataProviderRepository).findByName("Exchange Rates API");
        verify(currencyPairRepository).save(isA(CurrencyPair.class));
        verify(restTemplate)
                .getForObject(
                        eq(
                                "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=GBP&quote=GBP&data_type=chart&start_date=2020-03-01&end_date=2020-03-01"),
                        isA(Class.class),
                        isA(Object[].class));
    }

    /**
     * Test {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String, String)}.
     *
     * <ul>
     *   <li>Then calls {@link DataProviderRepository#save(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ExchangeRateServiceImpl#syncExchangeRates(String, String, String,
     * String)}
     */
    @Test
    @DisplayName("Test syncExchangeRates(String, String, String, String); then calls save(Object)")
    void testSyncExchangeRates_thenCallsSave2() throws RestClientException {
        // Arrange
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("UUU");
        baseCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        baseCurrency.setIsActive(true);
        baseCurrency.setMinorUnit((short) 1);
        baseCurrency.setName("Name");
        baseCurrency.setNumericCode((short) 1);
        baseCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        Currency quoteCurrency = new Currency();
        quoteCurrency.setCode("UUU");
        quoteCurrency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        quoteCurrency.setIsActive(true);
        quoteCurrency.setMinorUnit((short) 1);
        quoteCurrency.setName("Name");
        quoteCurrency.setNumericCode((short) 1);
        quoteCurrency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setBaseCurrency(baseCurrency);
        currencyPair.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currencyPair.setId(1L);
        currencyPair.setIsActive(true);
        currencyPair.setMaxSpread(new BigDecimal("2.3"));
        currencyPair.setMinSpread(new BigDecimal("2.3"));
        currencyPair.setPrecisionDigits((short) 1);
        currencyPair.setQuoteCurrency(quoteCurrency);
        currencyPair.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<CurrencyPair> ofResult = Optional.of(currencyPair);
        when(currencyPairRepository.findByBaseCurrencyCodeAndQuoteCurrencyCode(
                Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        DataProvider dataProvider = new DataProvider();
        dataProvider.setApiEndpoint("https://config.us-east-2.amazonaws.com");
        dataProvider.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        dataProvider.setId(1L);
        dataProvider.setIsActive(true);
        dataProvider.setLastSyncAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        dataProvider.setName("Name");
        dataProvider.setPriority((short) 1);
        dataProvider.setRateLimitPerHour(1);

        DataProvider dataProvider2 = new DataProvider();
        dataProvider2.setApiEndpoint("https://config.us-east-2.amazonaws.com");
        dataProvider2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        dataProvider2.setId(1L);
        dataProvider2.setIsActive(true);
        dataProvider2.setLastSyncAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        dataProvider2.setName("Name");
        dataProvider2.setPriority((short) 1);
        dataProvider2.setRateLimitPerHour(1);
        Optional<DataProvider> ofResult2 = Optional.of(dataProvider2);
        when(dataProviderRepository.save(Mockito.<DataProvider>any())).thenReturn(dataProvider);
        when(dataProviderRepository.findByName(Mockito.<String>any())).thenReturn(ofResult2);

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setAverageAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setAverageBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setBaseCurrency("GBP");
        exchangeRateData.setCloseTime("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setHighBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowAsk("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setLowBid("Starting sync for {} to {} from {} to {}");
        exchangeRateData.setQuoteCurrency("GBP");

        ArrayList<ExchangeRateData> response = new ArrayList<>();
        response.add(exchangeRateData);

        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setResponse(response);
        when(restTemplate.getForObject(
                Mockito.<String>any(),
                Mockito.<Class<ExchangeRateApiResponse>>any(),
                isA(Object[].class)))
                .thenReturn(exchangeRateApiResponse);

        // Act
        exchangeRateServiceImpl.syncExchangeRates("GBP", "GBP", "2020-03-01", "2020-03-01");

        // Assert
        verify(currencyPairRepository).findByBaseCurrencyCodeAndQuoteCurrencyCode("GBP", "GBP");
        verify(dataProviderRepository).findByName("Exchange Rates API");
        verify(dataProviderRepository).save(isA(DataProvider.class));
        verify(restTemplate)
                .getForObject(
                        eq(
                                "https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies?base=GBP&quote=GBP&data_type=chart&start_date=2020-03-01&end_date=2020-03-01"),
                        isA(Class.class),
                        isA(Object[].class));
    }
}
