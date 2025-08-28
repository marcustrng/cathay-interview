package com.cathay.inteview.tutqq.repository;

import com.cathay.inteview.tutqq.entity.DataProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataProviderRepository extends JpaRepository<DataProvider, Long> {
    Optional<DataProvider> findByName(String name);
    List<DataProvider> findByIsActiveTrueOrderByPriorityAsc();
}
