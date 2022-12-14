package com.example.crypto.controller;

import com.example.crypto.model.Crypto;
import com.example.crypto.service.CryptocurrencieService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finearz")
public class CryptocurrencieController {
    private CryptocurrencieService service;

    public CryptocurrencieController(CryptocurrencieService service) {
        this.service = service;
    }

    @GetMapping()
    public Page<Crypto> getCryoto(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "3") int size,
                                  @RequestParam(required = false) String symbol,
                                  @RequestParam(required = false) String sort,
                                  @RequestParam(required = false) String fiat
    ) {

        return service.getCrypto(page, size, symbol,sort,fiat);
    }



}

