package com.cathay.inteview.tutqq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Data
@Getter
@Entity
@Table(name = "api_clients")
@EntityListeners(AuditingEntityListener.class)
public class ApiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, unique = true)
    private UUID clientId = UUID.randomUUID();

    @NotBlank
    @Column(name = "api_key_hash", nullable = false, unique = true, length = 64)
    private String apiKeyHash;

    @NotBlank
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Tier tier = Tier.BASIC;

    @Column(name = "rate_limit_per_hour", nullable = false)
    private Integer rateLimitPerHour = 1000;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    public enum Tier {
        BASIC, PREMIUM, ENTERPRISE
    }
}

