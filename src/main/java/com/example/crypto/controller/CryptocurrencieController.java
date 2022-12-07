package com.example.crypto.controller;
import com.example.crypto.model.Cryptocurrencie;
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
    public Page<Cryptocurrencie> getCryoto(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size, @RequestParam() String symbol){

        return service.getCrypto(page,size,symbol);
    }
    @PostMapping("/get_email")
    public void getEmail(){
        service.getEmail();

    }



}

