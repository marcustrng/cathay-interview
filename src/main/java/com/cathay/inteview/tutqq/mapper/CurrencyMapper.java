package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.inteview.tutqq.entity.Currency;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CurrencyMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "asOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "asOffsetDateTime")
    CurrencyDto toDto(Currency entity);

    List<CurrencyDto> toDtoList(List<Currency> entities);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "asInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "asInstant")
    Currency toEntity(CurrencyDto request);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "asInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "asInstant")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CurrencyDto request, @MappingTarget Currency entity);

    @Named("asOffsetDateTime")
    default OffsetDateTime asOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    @Named("asInstant")
    default Instant asInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }
}

