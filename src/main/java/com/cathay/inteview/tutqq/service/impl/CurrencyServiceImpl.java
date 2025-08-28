package com.cathay.inteview.tutqq.service.impl;

import com.cathay.interview.tutqq.model.Currency;
import com.cathay.interview.tutqq.model.CurrencyCreateRequest;
import com.cathay.interview.tutqq.model.CurrencyUpdateRequest;
import com.cathay.inteview.tutqq.mapper.CurrencyMapper;
import com.cathay.inteview.tutqq.repository.CurrencyRepository;
import com.cathay.inteview.tutqq.service.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override
    public List<Currency> getAllCurrencies(Boolean isActive) {
        return currencyMapper.toDtoList((isActive == null)
                ? currencyRepository.findAll()
                : currencyRepository.findByIsActive(isActive));
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyRepository.findById(code)
                .map(currencyMapper::toDto);
    }

    @Override
    public Currency createCurrency(CurrencyCreateRequest currency) {
        if (currencyRepository.existsById(currency.getCode())) {
            throw new IllegalArgumentException("Currency with code " + currency.getCode() + " already exists");
        }
        return currencyMapper.toDto(currencyRepository.save(currencyMapper.toEntity(currency)));
    }

    @Override
    public Currency updateCurrency(String code, CurrencyUpdateRequest updatedCurrency) {
        return currencyRepository.findById(code)
                .map(existing -> {
                    existing.setName(updatedCurrency.getName());

                    if (updatedCurrency.getNumericCode() != null) {
                        existing.setNumericCode(updatedCurrency.getNumericCode().shortValue());
                    }

                    if (updatedCurrency.getMinorUnit() != null) {
                        existing.setMinorUnit(updatedCurrency.getMinorUnit().shortValue());
                    }

                    if (updatedCurrency.getIsActive() != null) {
                        existing.setIsActive(updatedCurrency.getIsActive());
                    }

                    return currencyRepository.save(existing);
                })
                .map(currencyMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Currency with code " + code + " not found"));
    }

    @Override
    public void deleteCurrency(String code) {
        currencyRepository.delete(currencyRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Currency with code " + code + " not found")));
    }
}
