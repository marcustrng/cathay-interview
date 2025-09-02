package com.cathay.inteview.tutqq.service.impl;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.interview.tutqq.model.ListCurrencies200Response;
import com.cathay.interview.tutqq.model.Pagination;
import com.cathay.inteview.tutqq.constants.PaginationConstants;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.mapper.CurrencyMapper;
import com.cathay.inteview.tutqq.repository.CurrencyRepository;
import com.cathay.inteview.tutqq.service.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override
    public ListCurrencies200Response listCurrencies(Boolean isActive, Integer page, Integer size, List<String> sort) {
        Pageable pageable = PageRequest.of(
                page == null ? PaginationConstants.DEFAULT_PAGE : page,
                size == null ? PaginationConstants.DEFAULT_PAGE_SIZE : Math.min(size, PaginationConstants.MAX_PAGE_SIZE),
                resolveSort(sort)
        );

        Page<Currency> currencyPage = (isActive == null)
                ? currencyRepository.findAll(pageable)
                : currencyRepository.findByIsActive(isActive, pageable);

        List<CurrencyDto> currencyDtos = currencyPage
                .map(currencyMapper::toDto)
                .getContent();

        Pagination pagination = buildPagination(currencyPage, isActive);

        return new ListCurrencies200Response()
                .content(currencyDtos)
                .pagination(pagination);
    }

    @Override
    public Optional<CurrencyDto> getCurrencyByCode(String code) {
        return currencyRepository.findById(code)
                .map(currencyMapper::toDto);
    }

    @Override
    public String createCurrency(CurrencyDto currencyCreateRequest) {
        if (currencyRepository.existsById(currencyCreateRequest.getCode())) {
            throw new IllegalArgumentException("Currency with code " + currencyCreateRequest.getCode() + " already exists");
        }
        CurrencyDto dto = currencyMapper.toDto(currencyRepository.save(currencyMapper.toEntity(currencyCreateRequest)));

        return dto.getCode();
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

    private Sort resolveSort(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by("code").ascending(); // default sort
        }

        // Expect sort params like ["name,asc", "createdAt,desc"]
        List<Sort.Order> orders = sort.stream()
                .map(order -> {
                    String[] parts = order.split(",");
                    String property = parts[0].trim();
                    Sort.Direction direction = parts.length > 1
                            ? Sort.Direction.fromString(parts[1].trim())
                            : Sort.Direction.ASC;
                    return new Sort.Order(direction, property);
                })
                .toList();

        return Sort.by(orders);
    }

    private Pagination buildPagination(Page<?> page, Boolean isActive) {
        Pagination pagination = new Pagination()
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .page(page.getNumber() + 1) // external API is usually 1-based
                .size(page.getSize());

        URI nextUrl = page.hasNext()
                ? URI.create(buildPageUrl(isActive, page.getNumber() + 1, page.getSize()))
                : null;

        URI prevUrl = page.hasPrevious()
                ? URI.create(buildPageUrl(isActive, page.getNumber() - 1, page.getSize()))
                : null;

        pagination.setNextUrl(JsonNullable.of(nextUrl));
        pagination.setPrevUrl(JsonNullable.of(prevUrl));

        return pagination;
    }

    private String buildPageUrl(Boolean isActive, int page, int size) {
        return String.format("/client-api/v1/currencies?isActive=%s&page=%d&size=%d",
                isActive, page, size);
    }
}
