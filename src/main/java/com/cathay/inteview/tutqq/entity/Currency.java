package com.cathay.inteview.tutqq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Entity
@Table(name = "currencies")
@EntityListeners(AuditingEntityListener.class)
public class Currency {

    @Id
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters")
    private String code;

    @NotBlank(message = "Currency name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Positive(message = "Numeric code must be positive")
    @Column(name = "numeric_code", nullable = false, unique = true)
    private Short numericCode;

    @Column(name = "minor_unit", nullable = false)
    private Short minorUnit = 2;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Currency() {
    }

    public Currency(String code) {
        this.code = code;
    }

    public Currency(String code, String name, Short numericCode) {
        this.code = code;
        this.name = name;
        this.numericCode = numericCode;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
