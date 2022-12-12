package com.example.crypto.service;

import com.example.crypto.model.Cryptocurrencie;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CryptoSearch {
    public static Specification<Cryptocurrencie> containIRT(Float priceUSDT) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.equal(cryptoRoot.get("priceUSDT"), priceUSDT);
        });

    }

    public static Specification<Cryptocurrencie> containUSDT(Float priceIRT) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.equal(cryptoRoot.get("priceIRT"), priceIRT);
        });

    }

    public static Specification<Cryptocurrencie> getSymbol(String symbol) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.like(cryptoRoot.get("symbol"), symbol);
        });
    }}
