package com.cathay.inteview.tutqq.service.impl;

import com.cathay.interview.tutqq.model.CurrencyCreateRequest;
import com.cathay.interview.tutqq.model.CurrencyUpdateRequest;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.mapper.CurrencyMapper;
import com.cathay.inteview.tutqq.repository.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CurrencyServiceImpl.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class CurrencyServiceImplTest {
    @MockBean
    private CurrencyMapper currencyMapper;

    @MockBean
    private CurrencyRepository currencyRepository;

    @Autowired
    private CurrencyServiceImpl currencyServiceImpl;

    /**
     * Test {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}.
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}
     */
    @Test
    @DisplayName("Test getAllCurrencies(Boolean)")
    void testGetAllCurrencies() {
        // Arrange
        when(currencyRepository.findByIsActive(Mockito.<Boolean>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> currencyServiceImpl.getAllCurrencies(true));
        verify(currencyRepository).findByIsActive(true);
    }

    /**
     * Test {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyMapper} {@link CurrencyMapper#toDtoList(List)} throw {@link
     *       IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}
     */
    @Test
    @DisplayName(
            "Test getAllCurrencies(Boolean); given CurrencyMapper toDtoList(List) throw IllegalArgumentException()")
    void testGetAllCurrencies_givenCurrencyMapperToDtoListThrowIllegalArgumentException() {
        // Arrange
        when(currencyRepository.findByIsActive(Mockito.<Boolean>any())).thenReturn(new ArrayList<>());
        when(currencyMapper.toDtoList(Mockito.<List<Currency>>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> currencyServiceImpl.getAllCurrencies(true));
        verify(currencyMapper).toDtoList(isA(List.class));
        verify(currencyRepository).findByIsActive(true);
    }

    /**
     * Test {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#findAll()} return {@link
     *       ArrayList#ArrayList()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}
     */
    @Test
    @DisplayName(
            "Test getAllCurrencies(Boolean); given CurrencyRepository findAll() return ArrayList()")
    void testGetAllCurrencies_givenCurrencyRepositoryFindAllReturnArrayList() {
        // Arrange
        when(currencyRepository.findAll()).thenReturn(new ArrayList<>());
        when(currencyMapper.toDtoList(Mockito.<List<Currency>>any())).thenReturn(new ArrayList<>());

        // Act
        List<com.cathay.interview.tutqq.model.Currency> actualAllCurrencies =
                currencyServiceImpl.getAllCurrencies(null);

        // Assert
        verify(currencyMapper).toDtoList(isA(List.class));
        verify(currencyRepository).findAll();
        assertTrue(actualAllCurrencies.isEmpty());
    }

    /**
     * Test {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#findAll()} throw {@link
     *       IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}
     */
    @Test
    @DisplayName(
            "Test getAllCurrencies(Boolean); given CurrencyRepository findAll() throw IllegalArgumentException()")
    void testGetAllCurrencies_givenCurrencyRepositoryFindAllThrowIllegalArgumentException() {
        // Arrange
        when(currencyRepository.findAll()).thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> currencyServiceImpl.getAllCurrencies(null));
        verify(currencyRepository).findAll();
    }

    /**
     * Test {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}.
     *
     * <ul>
     *   <li>Then return Empty.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getAllCurrencies(Boolean)}
     */
    @Test
    @DisplayName("Test getAllCurrencies(Boolean); then return Empty")
    void testGetAllCurrencies_thenReturnEmpty() {
        // Arrange
        when(currencyRepository.findByIsActive(Mockito.<Boolean>any())).thenReturn(new ArrayList<>());
        when(currencyMapper.toDtoList(Mockito.<List<Currency>>any())).thenReturn(new ArrayList<>());

        // Act
        List<com.cathay.interview.tutqq.model.Currency> actualAllCurrencies =
                currencyServiceImpl.getAllCurrencies(true);

        // Assert
        verify(currencyMapper).toDtoList(isA(List.class));
        verify(currencyRepository).findByIsActive(true);
        assertTrue(actualAllCurrencies.isEmpty());
    }

    /**
     * Test {@link CurrencyServiceImpl#getCurrencyByCode(String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyMapper} {@link CurrencyMapper#toDto(Currency)} return {@link
     *       com.cathay.interview.tutqq.model.Currency} (default constructor).
     *   <li>Then return Present.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getCurrencyByCode(String)}
     */
    @Test
    @DisplayName(
            "Test getCurrencyByCode(String); given CurrencyMapper toDto(Currency) return Currency (default constructor); then return Present")
    void testGetCurrencyByCode_givenCurrencyMapperToDtoReturnCurrency_thenReturnPresent() {
        // Arrange
        Currency currency = new Currency();
        currency.setCode("UUU");
        currency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency.setIsActive(true);
        currency.setMinorUnit((short) 1);
        currency.setName("Name");
        currency.setNumericCode((short) 1);
        currency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<Currency> ofResult = Optional.of(currency);
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(ofResult);
        com.cathay.interview.tutqq.model.Currency currency2 =
                new com.cathay.interview.tutqq.model.Currency();
        when(currencyMapper.toDto(Mockito.<Currency>any())).thenReturn(currency2);

        // Act
        Optional<com.cathay.interview.tutqq.model.Currency> actualCurrencyByCode =
                currencyServiceImpl.getCurrencyByCode("Code");

        // Assert
        verify(currencyMapper).toDto(isA(Currency.class));
        verify(currencyRepository).findById("Code");
        assertTrue(actualCurrencyByCode.isPresent());
        assertSame(currency2, actualCurrencyByCode.get());
    }

    /**
     * Test {@link CurrencyServiceImpl#getCurrencyByCode(String)}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#getCurrencyByCode(String)}
     */
    @Test
    @DisplayName("Test getCurrencyByCode(String); then throw IllegalArgumentException")
    void testGetCurrencyByCode_thenThrowIllegalArgumentException() {
        // Arrange
        when(currencyRepository.findById(Mockito.<String>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> currencyServiceImpl.getCurrencyByCode("Code"));
        verify(currencyRepository).findById("Code");
    }

    /**
     * Test {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}.
     *
     * <p>Method under test: {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}
     */
    @Test
    @DisplayName("Test createCurrency(CurrencyCreateRequest)")
    void testCreateCurrency() {
        // Arrange
        when(currencyRepository.existsById(Mockito.<String>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> currencyServiceImpl.createCurrency(new CurrencyCreateRequest()));
        verify(currencyRepository).existsById(null);
    }

    /**
     * Test {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}.
     *
     * <ul>
     *   <li>Given {@link Currency#Currency()} Code is {@code UUU}.
     *   <li>Then return {@link com.cathay.interview.tutqq.model.Currency} (default constructor).
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}
     */
    @Test
    @DisplayName(
            "Test createCurrency(CurrencyCreateRequest); given Currency() Code is 'UUU'; then return Currency (default constructor)")
    void testCreateCurrency_givenCurrencyCodeIsUuu_thenReturnCurrency() {
        // Arrange
        Currency currency = new Currency();
        currency.setCode("UUU");
        currency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency.setIsActive(true);
        currency.setMinorUnit((short) 1);
        currency.setName("Name");
        currency.setNumericCode((short) 1);
        currency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        when(currencyRepository.existsById(Mockito.<String>any())).thenReturn(false);
        when(currencyRepository.save(Mockito.<Currency>any())).thenReturn(currency);

        Currency currency2 = new Currency();
        currency2.setCode("UUU");
        currency2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency2.setIsActive(true);
        currency2.setMinorUnit((short) 1);
        currency2.setName("Name");
        currency2.setNumericCode((short) 1);
        currency2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        com.cathay.interview.tutqq.model.Currency currency3 =
                new com.cathay.interview.tutqq.model.Currency();
        when(currencyMapper.toDto(Mockito.<Currency>any())).thenReturn(currency3);
        when(currencyMapper.toEntity(Mockito.<CurrencyCreateRequest>any())).thenReturn(currency2);

        // Act
        com.cathay.interview.tutqq.model.Currency actualCreateCurrencyResult =
                currencyServiceImpl.createCurrency(new CurrencyCreateRequest());

        // Assert
        verify(currencyMapper).toDto(isA(Currency.class));
        verify(currencyMapper).toEntity(isA(CurrencyCreateRequest.class));
        verify(currencyRepository).existsById(null);
        verify(currencyRepository).save(isA(Currency.class));
        assertSame(currency3, actualCreateCurrencyResult);
    }

    /**
     * Test {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyMapper} {@link CurrencyMapper#toEntity(CurrencyCreateRequest)} throw
     *       {@link IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}
     */
    @Test
    @DisplayName(
            "Test createCurrency(CurrencyCreateRequest); given CurrencyMapper toEntity(CurrencyCreateRequest) throw IllegalArgumentException()")
    void testCreateCurrency_givenCurrencyMapperToEntityThrowIllegalArgumentException() {
        // Arrange
        when(currencyRepository.existsById(Mockito.<String>any())).thenReturn(false);
        when(currencyMapper.toEntity(Mockito.<CurrencyCreateRequest>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> currencyServiceImpl.createCurrency(new CurrencyCreateRequest()));
        verify(currencyMapper).toEntity(isA(CurrencyCreateRequest.class));
        verify(currencyRepository).existsById(null);
    }

    /**
     * Test {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#existsById(Object)} return
     *       {@code true}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#createCurrency(CurrencyCreateRequest)}
     */
    @Test
    @DisplayName(
            "Test createCurrency(CurrencyCreateRequest); given CurrencyRepository existsById(Object) return 'true'")
    void testCreateCurrency_givenCurrencyRepositoryExistsByIdReturnTrue() {
        // Arrange
        when(currencyRepository.existsById(Mockito.<String>any())).thenReturn(true);

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> currencyServiceImpl.createCurrency(new CurrencyCreateRequest()));
        verify(currencyRepository).existsById(null);
    }

    /**
     * Test {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyMapper} {@link CurrencyMapper#toDto(Currency)} return {@link
     *       com.cathay.interview.tutqq.model.Currency} (default constructor).
     *   <li>Then return {@link com.cathay.interview.tutqq.model.Currency} (default constructor).
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}
     */
    @Test
    @DisplayName(
            "Test updateCurrency(String, CurrencyUpdateRequest); given CurrencyMapper toDto(Currency) return Currency (default constructor); then return Currency (default constructor)")
    void testUpdateCurrency_givenCurrencyMapperToDtoReturnCurrency_thenReturnCurrency() {
        // Arrange
        Currency currency = new Currency();
        currency.setCode("UUU");
        currency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency.setIsActive(true);
        currency.setMinorUnit((short) 1);
        currency.setName("Name");
        currency.setNumericCode((short) 1);
        currency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<Currency> ofResult = Optional.of(currency);

        Currency currency2 = new Currency();
        currency2.setCode("UUU");
        currency2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency2.setIsActive(true);
        currency2.setMinorUnit((short) 1);
        currency2.setName("Name");
        currency2.setNumericCode((short) 1);
        currency2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        when(currencyRepository.save(Mockito.<Currency>any())).thenReturn(currency2);
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(ofResult);
        com.cathay.interview.tutqq.model.Currency currency3 =
                new com.cathay.interview.tutqq.model.Currency();
        when(currencyMapper.toDto(Mockito.<Currency>any())).thenReturn(currency3);

        // Act
        com.cathay.interview.tutqq.model.Currency actualUpdateCurrencyResult =
                currencyServiceImpl.updateCurrency("Code", new CurrencyUpdateRequest());

        // Assert
        verify(currencyMapper).toDto(isA(Currency.class));
        verify(currencyRepository).findById("Code");
        verify(currencyRepository).save(isA(Currency.class));
        assertSame(currency3, actualUpdateCurrencyResult);
    }

    /**
     * Test {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#findById(Object)} throw {@link
     *       IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}
     */
    @Test
    @DisplayName(
            "Test updateCurrency(String, CurrencyUpdateRequest); given CurrencyRepository findById(Object) throw IllegalArgumentException()")
    void testUpdateCurrency_givenCurrencyRepositoryFindByIdThrowIllegalArgumentException() {
        // Arrange
        when(currencyRepository.findById(Mockito.<String>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> currencyServiceImpl.updateCurrency("Code", new CurrencyUpdateRequest()));
        verify(currencyRepository).findById("Code");
    }

    /**
     * Test {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#save(Object)} throw {@link
     *       IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}
     */
    @Test
    @DisplayName(
            "Test updateCurrency(String, CurrencyUpdateRequest); given CurrencyRepository save(Object) throw IllegalArgumentException()")
    void testUpdateCurrency_givenCurrencyRepositorySaveThrowIllegalArgumentException() {
        // Arrange
        Currency currency = new Currency();
        currency.setCode("UUU");
        currency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency.setIsActive(true);
        currency.setMinorUnit((short) 1);
        currency.setName("Name");
        currency.setNumericCode((short) 1);
        currency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<Currency> ofResult = Optional.of(currency);
        when(currencyRepository.save(Mockito.<Currency>any()))
                .thenThrow(new IllegalArgumentException());
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> currencyServiceImpl.updateCurrency("Code", new CurrencyUpdateRequest()));
        verify(currencyRepository).findById("Code");
        verify(currencyRepository).save(isA(Currency.class));
    }

    /**
     * Test {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}.
     *
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#updateCurrency(String, CurrencyUpdateRequest)}
     */
    @Test
    @DisplayName(
            "Test updateCurrency(String, CurrencyUpdateRequest); then throw EntityNotFoundException")
    void testUpdateCurrency_thenThrowEntityNotFoundException() {
        // Arrange
        Optional<Currency> emptyResult = Optional.empty();
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> currencyServiceImpl.updateCurrency("Code", mock(CurrencyUpdateRequest.class)));
        verify(currencyRepository).findById("Code");
    }

    /**
     * Test {@link CurrencyServiceImpl#deleteCurrency(String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#delete(Object)} does nothing.
     *   <li>Then calls {@link CurrencyRepository#delete(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#deleteCurrency(String)}
     */
    @Test
    @DisplayName(
            "Test deleteCurrency(String); given CurrencyRepository delete(Object) does nothing; then calls delete(Object)")
    void testDeleteCurrency_givenCurrencyRepositoryDeleteDoesNothing_thenCallsDelete() {
        // Arrange
        Currency currency = new Currency();
        currency.setCode("UUU");
        currency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency.setIsActive(true);
        currency.setMinorUnit((short) 1);
        currency.setName("Name");
        currency.setNumericCode((short) 1);
        currency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<Currency> ofResult = Optional.of(currency);
        doNothing().when(currencyRepository).delete(Mockito.<Currency>any());
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        currencyServiceImpl.deleteCurrency("Code");

        // Assert
        verify(currencyRepository).delete(isA(Currency.class));
        verify(currencyRepository).findById("Code");
    }

    /**
     * Test {@link CurrencyServiceImpl#deleteCurrency(String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#delete(Object)} throw {@link
     *       IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#deleteCurrency(String)}
     */
    @Test
    @DisplayName(
            "Test deleteCurrency(String); given CurrencyRepository delete(Object) throw IllegalArgumentException()")
    void testDeleteCurrency_givenCurrencyRepositoryDeleteThrowIllegalArgumentException() {
        // Arrange
        Currency currency = new Currency();
        currency.setCode("UUU");
        currency.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        currency.setIsActive(true);
        currency.setMinorUnit((short) 1);
        currency.setName("Name");
        currency.setNumericCode((short) 1);
        currency.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Optional<Currency> ofResult = Optional.of(currency);
        doThrow(new IllegalArgumentException())
                .when(currencyRepository)
                .delete(Mockito.<Currency>any());
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> currencyServiceImpl.deleteCurrency("Code"));
        verify(currencyRepository).delete(isA(Currency.class));
        verify(currencyRepository).findById("Code");
    }

    /**
     * Test {@link CurrencyServiceImpl#deleteCurrency(String)}.
     *
     * <ul>
     *   <li>Given {@link CurrencyRepository} {@link CurrencyRepository#findById(Object)} throw {@link
     *       IllegalArgumentException#IllegalArgumentException()}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#deleteCurrency(String)}
     */
    @Test
    @DisplayName(
            "Test deleteCurrency(String); given CurrencyRepository findById(Object) throw IllegalArgumentException()")
    void testDeleteCurrency_givenCurrencyRepositoryFindByIdThrowIllegalArgumentException() {
        // Arrange
        when(currencyRepository.findById(Mockito.<String>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> currencyServiceImpl.deleteCurrency("Code"));
        verify(currencyRepository).findById("Code");
    }

    /**
     * Test {@link CurrencyServiceImpl#deleteCurrency(String)}.
     *
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.
     * </ul>
     *
     * <p>Method under test: {@link CurrencyServiceImpl#deleteCurrency(String)}
     */
    @Test
    @DisplayName("Test deleteCurrency(String); then throw EntityNotFoundException")
    void testDeleteCurrency_thenThrowEntityNotFoundException() {
        // Arrange
        Optional<Currency> emptyResult = Optional.empty();
        when(currencyRepository.findById(Mockito.<String>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> currencyServiceImpl.deleteCurrency("Code"));
        verify(currencyRepository).findById("Code");
    }
}
