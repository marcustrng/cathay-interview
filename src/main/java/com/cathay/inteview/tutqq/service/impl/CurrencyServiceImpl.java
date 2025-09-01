package com.cathay.inteview.tutqq.service.impl;

import com.cathay.interview.tutqq.model.CurrencyDto;
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
    public List<CurrencyDto> getAllCurrencies(Boolean isActive) {
        return currencyMapper.toDtoList((isActive == null)
                ? currencyRepository.findAll()
                : currencyRepository.findByIsActive(isActive));
    }

    @Override
    public Optional<CurrencyDto> getCurrencyByCode(String code) {
        return currencyRepository.findById(code)
                .map(currencyMapper::toDto);
    }

    @Override
    public CurrencyDto createCurrency(CurrencyDto currencyDto) {
        if (currencyRepository.existsById(currencyDto.getCode())) {
            throw new IllegalArgumentException("Currency with code " + currencyDto.getCode() + " already exists");
        }
        return currencyMapper.toDto(currencyRepository.save(currencyMapper.toEntity(currencyDto)));
    }

    @Override
    public CurrencyDto updateCurrency(String code, CurrencyDto updatedCurrency) {
        return currencyRepository.findById(code)
                .map(existing -> {
                    currencyMapper.updateEntityFromRequest(updatedCurrency, existing);
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
