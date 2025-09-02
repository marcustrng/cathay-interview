package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.interview.tutqq.model.ExchangeRateDtoRates;
import com.cathay.interview.tutqq.model.OHLC;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExchangeRateMapper {

    @Mapping(target = "date", source = "rateTimestamp",  qualifiedByName = "asOffsetDateTime")
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

    @Mapping(target = "currencyPair", source = "currencyPair")
    @Mapping(target = "provider", source = "provider")
    @Mapping(target = "rateTimestamp", source = "data.closeTime", qualifiedByName = "parseTimestamp")
    @Mapping(target = "bidOpen", source = "data.averageBid", qualifiedByName = "toBigDecimal")
    @Mapping(target = "bidHigh", source = "data.highBid", qualifiedByName = "toBigDecimal")
    @Mapping(target = "bidLow", source = "data.lowBid", qualifiedByName = "toBigDecimal")
    @Mapping(target = "bidClose", source = "data.averageBid", qualifiedByName = "toBigDecimal")
    @Mapping(target = "bidAverage", source = "data.averageBid", qualifiedByName = "toBigDecimal")
    @Mapping(target = "askOpen", source = "data.averageAsk", qualifiedByName = "toBigDecimal")
    @Mapping(target = "askHigh", source = "data.highAsk", qualifiedByName = "toBigDecimal")
    @Mapping(target = "askLow", source = "data.lowAsk", qualifiedByName = "toBigDecimal")
    @Mapping(target = "askClose", source = "data.averageAsk", qualifiedByName = "toBigDecimal")
    @Mapping(target = "askAverage", source = "data.averageAsk", qualifiedByName = "toBigDecimal")
    @Mapping(target = "midRate", source = "data", qualifiedByName = "calculateMidRate")
    @Mapping(target = "spread", source = "data", qualifiedByName = "calculateSpread")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ExchangeRate toEntity(ExchangeRateApiResponse.ExchangeRateData data,
                          CurrencyPair currencyPair,
                          DataProvider provider);

    // -------------------------------
    // Helper Methods
    // -------------------------------

    @Named("parseTimestamp")
    default Instant parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) return null;
        return OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
    }

    @Named("toBigDecimal")
    default BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid decimal value: " + value, ex);
        }
    }

    @Named("calculateMidRate")
    default BigDecimal calculateMidRate(ExchangeRateApiResponse.ExchangeRateData data) {
        BigDecimal bid = toBigDecimal(data.getAverageBid());
        BigDecimal ask = toBigDecimal(data.getAverageAsk());
        if (bid == null || ask == null) return null;
        return bid.add(ask).divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);
    }

    @Named("calculateSpread")
    default BigDecimal calculateSpread(ExchangeRateApiResponse.ExchangeRateData data) {
        BigDecimal bid = toBigDecimal(data.getAverageBid());
        BigDecimal ask = toBigDecimal(data.getAverageAsk());
        if (bid == null || ask == null) return null;
        return ask.subtract(bid);
    }

    @Named("asOffsetDateTime")
    default OffsetDateTime asOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    @Named("asInstant")
    default Instant asInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }
}
