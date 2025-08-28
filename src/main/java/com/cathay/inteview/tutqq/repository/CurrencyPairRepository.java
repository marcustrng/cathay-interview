package com.cathay.inteview.tutqq.repository;

import com.cathay.inteview.tutqq.entity.Currency;
import com.cathay.inteview.tutqq.entity.CurrencyPair;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyPairRepository extends JpaRepository<CurrencyPair, Long> {

    @Query("""
        SELECT cp FROM CurrencyPair cp
        JOIN FETCH cp.baseCurrency bc
        JOIN FETCH cp.quoteCurrency qc
        WHERE bc.code = :baseCurrency
        AND qc.code = :quoteCurrency
        AND cp.isActive = true
        """)
    Optional<CurrencyPair> findByBaseCurrencyCodeAndQuoteCurrencyCode(
            @Param("baseCurrency") String baseCurrency,
            @Param("quoteCurrency") String quoteCurrency
    );

    List<CurrencyPair> findByIsActiveTrue();

    Optional<CurrencyPair> findByBaseCurrencyAndQuoteCurrency(@NotNull Currency baseCurrency, @NotNull Currency quoteCurrency);

}
