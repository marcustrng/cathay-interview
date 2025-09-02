package com.cathay.inteview.tutqq.service;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.mapper.CurrencyMapper;
import com.cathay.inteview.tutqq.repository.CurrencyRepository;
import com.cathay.inteview.tutqq.service.impl.CurrencyServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {

    private CurrencyRepository currencyRepository;
    private CurrencyMapper currencyMapper;
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        currencyMapper = mock(CurrencyMapper.class);
        currencyService = new CurrencyServiceImpl(currencyRepository, currencyMapper);
    }

    @Test
    void testListCurrencies_returnsPagedDtos() {
        Currency currency = new Currency("USD");
        CurrencyDto dto = new CurrencyDto().code("USD");
        Page<Currency> page = new PageImpl<>(List.of(currency), PageRequest.of(0, 10), 1);

        when(currencyRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(currencyMapper.toDto(currency)).thenReturn(dto);

        ListCurrencies200Response response = currencyService.listCurrencies(null, 0, 10, null);

        assertThat(response.getContent()).hasSize(1).contains(dto);
        assertThat(response.getPagination().getTotalElements()).isEqualTo(1);
        verify(currencyRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetCurrencyByCode_found() {
        Currency currency = new Currency("EUR");
        CurrencyDto dto = new CurrencyDto().code("EUR");

        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(currency));
        when(currencyMapper.toDto(currency)).thenReturn(dto);

        Optional<CurrencyDto> result = currencyService.getCurrencyByCode("EUR");

        assertThat(result).isPresent().contains(dto);
    }

    @Test
    void testGetCurrencyByCode_notFound() {
        when(currencyRepository.findById("JPY")).thenReturn(Optional.empty());

        Optional<CurrencyDto> result = currencyService.getCurrencyByCode("JPY");

        assertThat(result).isEmpty();
    }

    @Test
    void testCreateCurrency_success() {
        CurrencyDto request = new CurrencyDto().code("GBP");
        Currency entity = new Currency("GBP");

        when(currencyRepository.existsById("GBP")).thenReturn(false);
        when(currencyMapper.toEntity(request)).thenReturn(entity);
        when(currencyRepository.save(entity)).thenReturn(entity);
        when(currencyMapper.toDto(entity)).thenReturn(request);

        String code = currencyService.createCurrency(request);

        assertThat(code).isEqualTo("GBP");
        verify(currencyRepository).save(entity);
    }

    @Test
    void testCreateCurrency_alreadyExists() {
        CurrencyDto request = new CurrencyDto().code("USD");

        when(currencyRepository.existsById("USD")).thenReturn(true);

        assertThatThrownBy(() -> currencyService.createCurrency(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currency with code USD already exists");
    }

    @Test
    void testUpdateCurrency_success() {
        Currency existing = new Currency("USD");
        CurrencyDto updateDto = new CurrencyDto().code("USD").name("Dollar");

        when(currencyRepository.findById("USD")).thenReturn(Optional.of(existing));
        when(currencyRepository.save(existing)).thenReturn(existing);
        when(currencyMapper.toDto(existing)).thenReturn(updateDto);

        CurrencyDto result = currencyService.updateCurrency("USD", updateDto);

        assertThat(result.getCode()).isEqualTo("USD");
        verify(currencyMapper).updateEntityFromRequest(updateDto, existing);
        verify(currencyRepository).save(existing);
    }

    @Test
    void testUpdateCurrency_notFound() {
        CurrencyDto updateDto = new CurrencyDto().code("JPY");

        when(currencyRepository.findById("JPY")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.updateCurrency("JPY", updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Currency with code JPY not found");
    }

    @Test
    void testDeleteCurrency_success() {
        Currency existing = new Currency("EUR");

        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(existing));

        currencyService.deleteCurrency("EUR");

        verify(currencyRepository).delete(existing);
    }

    @Test
    void testDeleteCurrency_notFound() {
        when(currencyRepository.findById("GBP")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.deleteCurrency("GBP"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Currency with code GBP not found");
    }
}
