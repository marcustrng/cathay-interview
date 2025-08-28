package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.Currency;
import com.cathay.interview.tutqq.model.CurrencyCreateRequest;
import com.cathay.interview.tutqq.model.CurrencyUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {DateMapper.class}
)
public interface CurrencyMapper {

    Currency toDto(com.cathay.inteview.tutqq.entity.Currency entity);

    List<Currency> toDtoList(List<com.cathay.inteview.tutqq.entity.Currency> entities);

    com.cathay.inteview.tutqq.entity.Currency toEntity(CurrencyCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CurrencyUpdateRequest request, @MappingTarget com.cathay.inteview.tutqq.entity.Currency entity);
}
