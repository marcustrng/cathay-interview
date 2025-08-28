package com.cathay.inteview.tutqq.repository;

import com.cathay.inteview.tutqq.entity.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, Long> {

    @Query("SELECT ac FROM ApiClient ac WHERE ac.apiKeyHash = :apiKeyHash AND ac.isActive = true")
    Optional<ApiClient> findByApiKeyHashAndIsActiveTrue(@Param("apiKeyHash") String apiKeyHash);

    @Query("""
        SELECT ac FROM ApiClient ac 
        WHERE ac.apiKeyHash = :apiKeyHash 
        AND ac.isActive = true 
        AND (ac.expiresAt IS NULL OR ac.expiresAt > :now)
        """)
    Optional<ApiClient> findValidApiClient(@Param("apiKeyHash") String apiKeyHash, @Param("now") Instant now);
}
