package com.example.crypto.service;

import com.example.crypto.model.Crypto;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@EnableAsync
public class CryptoMarket {
    private CryptocurrencieRepository repository;
    private EmailService emailService;
    private JSONArray cryptoJson;
    private Crypto crypto;
    //if giveAllCryptos is true, get all market. that work when you run app for first and at 8:00PM.
    private static Boolean giveAllCryptos = true;
    private static List<Float> lastChangesTaken = new ArrayList<>();
    private List<Crypto> cryptos = new ArrayList<>();


    public CryptoMarket(CryptocurrencieRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void getAllMarketAt8() {
        giveAllCryptos = true;
    }

    @Async
    @Scheduled(fixedRate = 20000)
    public void getCryptocurrencieIRT() throws IOException, JSONException {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("https://api.finearz.net/api/v1/market/?", String.class);
        String crypto = responseEntity.getBody();
        cryptoJson = new JSONArray(crypto);

        getSymbols();
        getPrice();

        if (giveAllCryptos == true) {
            cryptos = repository.saveAll(cryptos);
            giveAllCryptos = false;
        }
    }

    public JSONObject getJsonMarketCrypto(int id) throws JSONException {
        return cryptoJson.getJSONObject(id).getJSONObject("market").getJSONObject("crypto");
    }

    public JSONObject getJsonMarketInfo(int id) throws JSONException {
        return cryptoJson.getJSONObject(id).getJSONObject("marketInfo");
    }


    public void getPrice() throws JSONException {

        String lastSymbol = "";

        for (int i = 0; i < cryptoJson.length(); i++) {

            if (giveAllCryptos == true) {
                if (cryptos.get(i).getSymbol().equals(lastSymbol)) {
                    setMarket("USDT", i);

                } else {
                    setMarket("IRT", i);
                }
                lastSymbol = cryptos.get(i).getSymbol();
            }
            //get changing percent every 20 seconds
            float changePercent = (
                    (Float.parseFloat(
                            getJsonMarketInfo(i).getString("lastPrice")) - lastChangesTaken.get(i)) * 100
            ) / lastChangesTaken.get(i);

            if (changePercent >= 0.05 || changePercent <= -0.05) {
                lastChangesTaken.set(i,
                        Float.valueOf(
                                getJsonMarketInfo(i).getString("lastDayChange")
                        )
                );

                crypto = repository.findById((long) (i + 1)).get();

                crypto.setLastDayChange(
                        Float.parseFloat(getJsonMarketInfo(i).getString("lastDayChange"))
                );

                crypto.setPrice(
                        Float.parseFloat(
                                getJsonMarketInfo(i).getString("lastPrice")
                        )
                );

                repository.save(crypto);

                emailService.sendEmail(
                        crypto.getName() + " change around " + changePercent + "%");
            }
        }
    }

    public void setMarket(String fiat, int i) throws JSONException {
        cryptos.get(i).setPrice(Float.parseFloat(
                getJsonMarketInfo(i).getString("lastPrice")
        ));

        cryptos.get(i).setLastDayChange(Float.parseFloat(
                        getJsonMarketInfo(i).getString("lastDayChange")
                )
        );

        cryptos.get(i).setFiat(fiat);

        lastChangesTaken.add(cryptos.get(i).getPrice());
    }

    public void getSymbols() throws JSONException {
        for (int i = 0; i < cryptoJson.length(); i++) {
            Crypto crypto = new Crypto();
            crypto.setSymbol(getJsonMarketCrypto(i).getString("symbol"));
            crypto.setName(getJsonMarketCrypto(i).getString("name"));
            if (giveAllCryptos == true) {
                cryptos.add(crypto);
            }
        }
    }

}
