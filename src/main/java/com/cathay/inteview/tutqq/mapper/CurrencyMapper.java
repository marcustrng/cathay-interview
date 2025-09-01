package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.inteview.tutqq.entity.Currency;
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

    CurrencyDto toDto(Currency entity);

    List<CurrencyDto> toDtoList(List<Currency> entities);

    Currency toEntity(CurrencyDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CurrencyDto request, @MappingTarget Currency entity);
}
