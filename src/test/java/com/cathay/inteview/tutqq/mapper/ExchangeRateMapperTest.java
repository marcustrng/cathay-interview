package com.cathay.inteview.tutqq.mapper;

import com.cathay.interview.tutqq.model.ExchangeRateDto;
import com.cathay.interview.tutqq.model.OHLC;
import com.cathay.inteview.tutqq.client.dto.ExchangeRateApiResponse;
import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import com.cathay.inteview.tutqq.entity.DataProvider;
import com.cathay.inteview.tutqq.entity.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateMapperTest {

    private ExchangeRateMapper exchangeRateMapper;

    @BeforeEach
    void setUp() {
        exchangeRateMapper = Mappers.getMapper(ExchangeRateMapper.class);
    }

    @Test
    void toDto_ShouldMapExchangeRateToDto() {
        CurrencyPair pair = new CurrencyPair(new Currency("EUR"), new Currency("USD"));
        ExchangeRate rate = new ExchangeRate();
        rate.setCurrencyPair(pair);
        rate.setRateTimestamp(Instant.now());
        rate.setBidOpen(BigDecimal.valueOf(1.1));
        rate.setBidHigh(BigDecimal.valueOf(1.2));
        rate.setBidLow(BigDecimal.valueOf(1.0));
        rate.setBidClose(BigDecimal.valueOf(1.15));
        rate.setBidAverage(BigDecimal.valueOf(1.12));
        rate.setAskOpen(BigDecimal.valueOf(1.2));
        rate.setAskHigh(BigDecimal.valueOf(1.25));
        rate.setAskLow(BigDecimal.valueOf(1.15));
        rate.setAskClose(BigDecimal.valueOf(1.22));
        rate.setAskAverage(BigDecimal.valueOf(1.2));
        rate.setMidRate(BigDecimal.valueOf(1.16));
        rate.setSpread(BigDecimal.valueOf(0.08));

        ExchangeRateDto dto = exchangeRateMapper.toDto(rate);

        assertThat(dto).isNotNull();
        assertThat(dto.getBaseCurrency()).isEqualTo("EUR");
        assertThat(dto.getQuoteCurrency()).isEqualTo("USD");
        assertThat(dto.getDate()).isEqualTo(rate.getRateTimestamp().atOffset(ZoneOffset.UTC));
        assertThat(dto.getRates().getBid().getHigh()).isEqualTo(rate.getBidHigh());
        assertThat(dto.getRates().getAsk().getLow()).isEqualTo(rate.getAskLow());
        assertThat(dto.getRates().getMid()).isEqualTo(rate.getMidRate());
        assertThat(dto.getRates().getSpread()).isEqualTo(rate.getSpread());
    }

    @Test
    void toDtoList_ShouldMapListCorrectly() {
        ExchangeRate rate1 = new ExchangeRate();
        rate1.setRateTimestamp(Instant.now());
        ExchangeRate rate2 = new ExchangeRate();
        rate2.setRateTimestamp(Instant.now().plusSeconds(3600));

        List<ExchangeRateDto> dtos = exchangeRateMapper.toDtoList(List.of(rate1, rate2));
        assertThat(dtos).hasSize(2);
    }

    @Test
    void toBidOHLC_ShouldMapCorrectly() {
        ExchangeRate rate = new ExchangeRate();
        rate.setBidOpen(BigDecimal.valueOf(1.1));
        rate.setBidHigh(BigDecimal.valueOf(1.2));
        rate.setBidLow(BigDecimal.valueOf(1.0));
        rate.setBidClose(BigDecimal.valueOf(1.15));
        rate.setBidAverage(BigDecimal.valueOf(1.12));

        OHLC ohlc = exchangeRateMapper.toBidOHLC(rate);
        assertThat(ohlc.getOpen()).isEqualTo(rate.getBidOpen());
        assertThat(ohlc.getHigh()).isEqualTo(rate.getBidHigh());
        assertThat(ohlc.getLow()).isEqualTo(rate.getBidLow());
        assertThat(ohlc.getClose()).isEqualTo(rate.getBidClose());
        assertThat(ohlc.getAverage()).isEqualTo(rate.getBidAverage());
    }

    @Test
    void toAskOHLC_ShouldMapCorrectly() {
        ExchangeRate rate = new ExchangeRate();
        rate.setAskOpen(BigDecimal.valueOf(1.2));
        rate.setAskHigh(BigDecimal.valueOf(1.25));
        rate.setAskLow(BigDecimal.valueOf(1.15));
        rate.setAskClose(BigDecimal.valueOf(1.22));
        rate.setAskAverage(BigDecimal.valueOf(1.2));

        OHLC ohlc = exchangeRateMapper.toAskOHLC(rate);
        assertThat(ohlc.getOpen()).isEqualTo(rate.getAskOpen());
        assertThat(ohlc.getHigh()).isEqualTo(rate.getAskHigh());
        assertThat(ohlc.getLow()).isEqualTo(rate.getAskLow());
        assertThat(ohlc.getClose()).isEqualTo(rate.getAskClose());
        assertThat(ohlc.getAverage()).isEqualTo(rate.getAskAverage());
    }

    @Test
    void toEntity_ShouldMapApiResponseToEntity() {
        ExchangeRateApiResponse.ExchangeRateData data = new ExchangeRateApiResponse.ExchangeRateData();
        data.setCloseTime("2025-03-26T23:59:59Z");
        data.setAverageBid("1.1");
        data.setHighBid("1.2");
        data.setLowBid("1.0");
        data.setAverageAsk("1.2");
        data.setHighAsk("1.25");
        data.setLowAsk("1.15");

        CurrencyPair pair = new CurrencyPair(new Currency("EUR"), new Currency("USD"));
        DataProvider provider = new DataProvider("OANDA", "http://api.example.com");

        ExchangeRate entity = exchangeRateMapper.toEntity(data, pair, provider);

        assertThat(entity).isNotNull();
        assertThat(entity.getCurrencyPair()).isEqualTo(pair);
        assertThat(entity.getProvider()).isEqualTo(provider);
        assertThat(entity.getBidOpen()).isEqualTo(BigDecimal.valueOf(1.1));
        assertThat(entity.getAskHigh()).isEqualTo(BigDecimal.valueOf(1.25));
        assertThat(entity.getRateTimestamp()).isNotNull();
    }
}
