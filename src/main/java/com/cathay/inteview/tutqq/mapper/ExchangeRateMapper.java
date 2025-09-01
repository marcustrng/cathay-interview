package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.interview.tutqq.model.ExchangeRateDtoRates;
import com.cathay.interview.tutqq.model.OHLC;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {DateMapper.class}
)
public interface ExchangeRateMapper {

    @Mapping(target = "date", source = "rateTimestamp")
    @Mapping(target = "baseCurrency", source = "currencyPair.baseCurrency.code")
    @Mapping(target = "quoteCurrency", source = "currencyPair.quoteCurrency.code")
    @Mapping(target = "rates", source = ".")
    ExchangeRateDto toDto(ExchangeRate exchangeRate);

    List<ExchangeRateDto> toDtoList(List<ExchangeRate> exchangeRates);

    @Mapping(target = "open", source = "bidOpen")
    @Mapping(target = "high", source = "bidHigh")
    @Mapping(target = "low", source = "bidLow")
    @Mapping(target = "close", source = "bidClose")
    @Mapping(target = "average", source = "bidAverage")
    OHLC toBidOHLC(ExchangeRate exchangeRate);

    @Mapping(target = "open", source = "askOpen")
    @Mapping(target = "high", source = "askHigh")
    @Mapping(target = "low", source = "askLow")
    @Mapping(target = "close", source = "askClose")
    @Mapping(target = "average", source = "askAverage")
    OHLC toAskOHLC(ExchangeRate exchangeRate);

    default ExchangeRateDtoRates toExchangeRatesDto(ExchangeRate source) {
        if (source == null) return null;

        return new ExchangeRateDtoRates(
                toBidOHLC(source),
                toAskOHLC(source),
                source.getMidRate(),
                source.getSpread()
        );
    }
}
