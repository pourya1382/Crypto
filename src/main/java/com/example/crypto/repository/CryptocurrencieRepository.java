package com.example.crypto.repository;

import com.example.crypto.model.Cryptocurrencie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptocurrencieRepository extends JpaRepository<Cryptocurrencie,Long> {
    Page<Cryptocurrencie> findCryptocurrencieBySymbol(String symbol, Pageable pageable);

}