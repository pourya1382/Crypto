package com.example.crypto.service;


import com.example.crypto.model.Crypto;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CryptocurrencieService {
    private CryptocurrencieRepository repository;


    public CryptocurrencieService(CryptocurrencieRepository repository) {
        this.repository = repository;
    }

    public Page<Crypto> getCrypto(int page, int size, String symbol, String sort, String fiat) {
        System.out.println("symbol: " + symbol);
        Pageable pageable = PageRequest.of(page, size);

        if (!sort.isEmpty()) {
            switch (sort) {
                case "sort_by_price":
                    pageable = PageRequest.of(page, size, Sort.by("price").descending());
                    break;
                case "sort_by_price_descending":
                    pageable = PageRequest.of(page, size, Sort.by("price"));
                    break;
            }
        }
        Page<Crypto> cryptos;
        cryptos = repository.findAll(pageable);
        if (!symbol.isEmpty() || !fiat.isEmpty()) {
            cryptos = repository.findAll(Specification.where(CryptoSearch.getSymbol(symbol)
                            .and(CryptoSearch.searchFiat(fiat))),
                    pageable);
        }
        if (cryptos.isEmpty()) {
            cryptos = repository.findAll(Specification.where(CryptoSearch.getSymbol(symbol)
                            .or(CryptoSearch.searchFiat(fiat))),
                    pageable);
        }

        return cryptos;
    }


    public Page<Crypto> getAllCryptos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }
}
