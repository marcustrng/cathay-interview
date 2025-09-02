package com.cathay.inteview.tutqq.repository;

import com.cathay.inteview.tutqq.entity.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {
    List<Currency> findByIsActiveTrue();

    Page<Currency> findByIsActive(Boolean isActive, Pageable pageable);
}
