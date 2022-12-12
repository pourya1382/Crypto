package com.example.crypto.service;


import com.example.crypto.model.Cryptocurrencie;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CryptocurrencieService {
    private CryptocurrencieRepository repository;
    private CryptoChanges changes;


    public CryptocurrencieService(CryptocurrencieRepository repository,
                                  CryptoChanges changes
    ) {
        this.repository = repository;
        this.changes = changes;
    }

    public Page<Cryptocurrencie> getCrypto(int page, int size, String symbol, String sort, String fiat) {
        Pageable pageable = PageRequest.of(page, size);
        Float priceIRT;
        Float priceUSDT;
        if (!sort.isEmpty()) {
            switch (sort) {
                case "sort_by_price":
                    pageable = PageRequest.of(page, size, Sort.by("priceIRT").descending());
                    break;
                case "sort_by_price_descending":
                    pageable = PageRequest.of(page, size, Sort.by("priceIRT"));
                    break;
            }
        }
        Page<Cryptocurrencie> cryptos;
        if (fiat.equals("IRT")) {
            priceUSDT = Float.valueOf(0);
            cryptos = repository.findAll(Specification.where(CryptoSearch.containIRT(priceUSDT))
                    .or(CryptoSearch.getSymbol(symbol)), pageable);
        } else if (fiat.equals("USDT")) {
            priceIRT = Float.valueOf(0);
            cryptos = repository.findAll(Specification.where(CryptoSearch.containUSDT(priceIRT))
                    .or(CryptoSearch.getSymbol(symbol)), pageable);

        } else {
            cryptos = repository.findAll(Specification.where(CryptoSearch.getSymbol(symbol)),
                    pageable);
        }

        return cryptos;
    }

    public void getEmail() {
        changes.start();
    }


}
