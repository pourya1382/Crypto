package com.example.crypto.service;

import com.example.crypto.model.Crypto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CryptoSearch {
    public static Specification<Crypto> containIRT(Float priceUSDT) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.equal(cryptoRoot.get("priceUSDT"), priceUSDT);
        });

    }

    public static Specification<Crypto> containUSDT(Float priceIRT) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.equal(cryptoRoot.get("priceIRT"), priceIRT);
        });

    }

    public static Specification<Crypto> getSymbol(String symbol) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.like(cryptoRoot.get("symbol"), symbol);
        });

    }

    public static Specification<Crypto> searchFiat(String fiat) {
        return ((cryptoRoot, cq, cb) -> {
            return cb.equal(cryptoRoot.get("fiat"), fiat);
        });
    }
}
