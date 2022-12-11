package com.example.crypto.service;


import com.example.crypto.model.Cryptocurrencie;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CryptocurrencieService {
    private CryptocurrencieRepository repository;
    private  CryptoChanges changes;

    public CryptocurrencieService(CryptocurrencieRepository repository, CryptoChanges changes) {
        this.repository = repository;
        this.changes = changes;
    }


    public Page<Cryptocurrencie> getCrypto(int page, int size, String symbol) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cryptocurrencie> cryptos;
        if (symbol.isEmpty()) {
            cryptos = repository.findAll(pageable);
        }else {
            cryptos = repository.findCryptocurrencieBySymbol(symbol,pageable);
        }
        return cryptos;
    }
    public void getEmail(){
        changes.start();
    }


}
