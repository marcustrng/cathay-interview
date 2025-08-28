package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.ExchangeRateData;
import com.cathay.interview.tutqq.model.ExchangeRates;
import com.cathay.interview.tutqq.model.OHLC;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { DateMapper.class }
)
public interface ExchangeRateMapper {

    @Mapping(target = "date", source = "rateTimestamp")
    @Mapping(target = "baseCurrency", source = "currencyPair.baseCurrency.code")
    @Mapping(target = "quoteCurrency", source = "currencyPair.quoteCurrency.code")
    @Mapping(target = "rates", source = ".")
    ExchangeRateData toDto(ExchangeRate exchangeRate);

    List<ExchangeRateData> toDtoList(List<ExchangeRate> exchangeRates);

//    @Mapping(target = "bid", source = ".")
//    @Mapping(target = "ask", source = ".")
//    @Mapping(target = "mid", source = "midRate")
//    @Mapping(target = "spread", source = "spread")
//    ExchangeRates toExchangeRatesDto(ExchangeRate exchangeRate);

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

    default ExchangeRates toExchangeRatesDto(ExchangeRate source) {
        if (source == null) return null;

        return new ExchangeRates(
                toBidOHLC(source),
                toAskOHLC(source),
                source.getMidRate().toString(),
                source.getSpread().toString()
        );
    }
}
