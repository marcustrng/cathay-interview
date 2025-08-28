package com.cathay.inteview.tutqq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "exchange_rates",
        indexes = {
                @Index(name = "idx_exchange_rates_lookup", columnList = "currency_pair_id, rate_timestamp"),
                @Index(name = "idx_exchange_rates_timestamp", columnList = "rate_timestamp")
        })
@EntityListeners(AuditingEntityListener.class)
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_pair_id", nullable = false)
    private CurrencyPair currencyPair;

    @NotNull
    @Column(name = "rate_timestamp", nullable = false)
    private Instant rateTimestamp;

    // Bid OHLC data
    @Positive
    @Column(name = "bid_open", nullable = false, precision = 15, scale = 8)
    private BigDecimal bidOpen;

    @Positive
    @Column(name = "bid_high", nullable = false, precision = 15, scale = 8)
    private BigDecimal bidHigh;

    @Positive
    @Column(name = "bid_low", nullable = false, precision = 15, scale = 8)
    private BigDecimal bidLow;

    @Positive
    @Column(name = "bid_close", nullable = false, precision = 15, scale = 8)
    private BigDecimal bidClose;

    @Positive
    @Column(name = "bid_average", nullable = false, precision = 15, scale = 8)
    private BigDecimal bidAverage;

    // Ask OHLC data
    @Positive
    @Column(name = "ask_open", nullable = false, precision = 15, scale = 8)
    private BigDecimal askOpen;

    @Positive
    @Column(name = "ask_high", nullable = false, precision = 15, scale = 8)
    private BigDecimal askHigh;

    @Positive
    @Column(name = "ask_low", nullable = false, precision = 15, scale = 8)
    private BigDecimal askLow;

    @Positive
    @Column(name = "ask_close", nullable = false, precision = 15, scale = 8)
    private BigDecimal askClose;

    @Positive
    @Column(name = "ask_average", nullable = false, precision = 15, scale = 8)
    private BigDecimal askAverage;

    // Derived values
    @Column(name = "mid_rate", precision = 15, scale = 8)
    private BigDecimal midRate;

    @Column(name = "spread", precision = 15, scale = 8)
    private BigDecimal spread;

    // Metadata
    @Column(name = "volume", precision = 20, scale = 2)
    private BigDecimal volume = BigDecimal.ZERO;

    @Column(name = "trade_count")
    private Integer tradeCount = 0;

    @Column(name = "data_quality_score")
    private Short dataQualityScore = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private DataProvider provider;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Calculate derived values before persist/update
    @PrePersist
    @PreUpdate
    public void calculateDerivedValues() {
        if (bidClose != null && askClose != null) {
            this.midRate = bidClose.add(askClose).divide(BigDecimal.valueOf(2));
            this.spread = askClose.subtract(bidClose);
        }
    }
}
