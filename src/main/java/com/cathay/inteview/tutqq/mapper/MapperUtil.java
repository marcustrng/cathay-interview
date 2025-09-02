package com.cathay.inteview.tutqq.mapper;

import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class MapperUtil {

    public OffsetDateTime asOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    public Instant asInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }

    public static  Instant parseTimestamp(String timestamp) {
        LocalDateTime ldt = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return ldt.atZone(ZoneOffset.UTC).toInstant();
    }

    public static BigDecimal toBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    public static  BigDecimal calculateMidRate(ExchangeRateApiResponse.ExchangeRateData data) {
        BigDecimal bid = toBigDecimal(data.getAverageBid());
        BigDecimal ask = toBigDecimal(data.getAverageAsk());
        return (bid != null && ask != null)
                ? bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
                : null;
    }

    public static  BigDecimal calculateSpread(ExchangeRateApiResponse.ExchangeRateData data) {
        BigDecimal bid = toBigDecimal(data.getAverageBid());
        BigDecimal ask = toBigDecimal(data.getAverageAsk());
        return (bid != null && ask != null) ? ask.subtract(bid) : null;
    }

}
