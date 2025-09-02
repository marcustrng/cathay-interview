package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.CurrencyDto;
import com.cathay.inteview.tutqq.entity.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyMapperTest {

    private CurrencyMapper currencyMapper;

    @BeforeEach
    void setUp() {
        currencyMapper = Mappers.getMapper(CurrencyMapper.class);
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        Currency entity = new Currency();
        entity.setCode("USD");
        entity.setName("US Dollar");
        entity.setIsActive(true);

        CurrencyDto dto = currencyMapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getCode()).isEqualTo("USD");
        assertThat(dto.getName()).isEqualTo("US Dollar");
        assertThat(dto.getIsActive()).isTrue();
    }

    @Test
    void toDtoList_ShouldMapEntitiesToDtos() {
        Currency entity1 = new Currency();
        entity1.setCode("USD");
        entity1.setName("US Dollar");

        Currency entity2 = new Currency();
        entity2.setCode("EUR");
        entity2.setName("Euro");

        List<CurrencyDto> dtos = currencyMapper.toDtoList(List.of(entity1, entity2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getCode()).isEqualTo("USD");
        assertThat(dtos.get(1).getCode()).isEqualTo("EUR");
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        CurrencyDto dto = new CurrencyDto();
        dto.setCode("JPY");
        dto.setName("Japanese Yen");
        dto.setIsActive(false);

        Currency entity = currencyMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getCode()).isEqualTo("JPY");
        assertThat(entity.getName()).isEqualTo("Japanese Yen");
        assertThat(entity.getIsActive()).isFalse();
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateNonNullFieldsOnly() {
        CurrencyDto dto = new CurrencyDto();
        dto.setName("Updated Name");  // only updating name
        dto.setIsActive(true);        // updating active

        Currency entity = new Currency();
        entity.setCode("USD");         // should remain unchanged
        entity.setName("Old Name");
        entity.setIsActive(false);

        currencyMapper.updateEntityFromRequest(dto, entity);

        assertThat(entity.getCode()).isEqualTo("USD"); // unchanged
        assertThat(entity.getName()).isEqualTo("Updated Name");
        assertThat(entity.getIsActive()).isTrue();
    }
}
