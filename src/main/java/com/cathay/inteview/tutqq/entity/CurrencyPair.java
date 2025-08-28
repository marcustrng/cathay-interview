package com.cathay.inteview.tutqq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "currency_pairs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"base_currency", "quote_currency"}))
@EntityListeners(AuditingEntityListener.class)
public class CurrencyPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_currency", referencedColumnName = "code", nullable = false)
    private Currency baseCurrency;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_currency", referencedColumnName = "code", nullable = false)
    private Currency quoteCurrency;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "min_spread", precision = 10, scale = 5)
    private BigDecimal minSpread;

    @Column(name = "max_spread", precision = 10, scale = 5)
    private BigDecimal maxSpread;

    @Column(name = "precision_digits", nullable = false)
    private Short precisionDigits = 5;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CurrencyPair() {
    }

    public CurrencyPair(Currency baseCurrency, Currency quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public CurrencyPair(String baseCurrency, String quoteCurrency) {
        this(new Currency(baseCurrency), new Currency(quoteCurrency));
    }
}
