package com.cathay.inteview.tutqq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Entity
@Table(name = "data_providers")
@EntityListeners(AuditingEntityListener.class)
public class DataProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "api_endpoint", length = 500)
    private String apiEndpoint;

    @Column(nullable = false)
    private Short priority = 100;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "rate_limit_per_hour")
    private Integer rateLimitPerHour = 1000;

    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DataProvider() {
        this.createdAt = Instant.now();
    }

    public DataProvider(String name, String apiEndpoint) {
        this();
        this.name = name;
        this.apiEndpoint = apiEndpoint;
    }

}

