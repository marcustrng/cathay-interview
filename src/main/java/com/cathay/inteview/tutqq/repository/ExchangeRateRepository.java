package com.cathay.inteview.tutqq.repository;

import com.cathay.inteview.tutqq.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("""
            SELECT er FROM ExchangeRate er
            JOIN FETCH er.currencyPair cp
            JOIN FETCH cp.baseCurrency bc
            JOIN FETCH cp.quoteCurrency qc
            WHERE bc.code = :baseCurrency
            AND qc.code = :quoteCurrency
            AND er.rateTimestamp >= :startDate
            AND er.rateTimestamp <= :endDate
            ORDER BY er.rateTimestamp DESC
            """)
    List<ExchangeRate> findExchangeRatesByCurrencyPairAndDateRange(
            @Param("baseCurrency") String baseCurrency,
            @Param("quoteCurrency") String quoteCurrency,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
            SELECT er FROM ExchangeRate er
            JOIN er.currencyPair cp
            WHERE cp.id = :currencyPairId
            AND er.rateTimestamp >= :startDate
            AND er.rateTimestamp <= :endDate
            ORDER BY er.rateTimestamp DESC
            """)
    List<ExchangeRate> findByCurrencyPairIdAndDateRange(
            @Param("currencyPairId") Long currencyPairId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
            SELECT er FROM ExchangeRate er
            JOIN er.currencyPair cp
            WHERE cp.id = :currencyPairId
            AND er.rateTimestamp = :rateTimestamp
            """)
    Optional<ExchangeRate> findByPairAndTimestamp(@Param("currencyPairId") Long currencyPairId,
                                                  @Param("rateTimestamp") Instant rateTimestamp);

    @Query("SELECT er FROM ExchangeRate er WHERE er.rateTimestamp >= ?1 ORDER BY er.rateTimestamp DESC")
    List<ExchangeRate> findRecentRates(LocalDateTime since);

}
