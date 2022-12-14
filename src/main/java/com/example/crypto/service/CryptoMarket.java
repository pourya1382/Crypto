package com.example.crypto.service;

import com.example.crypto.model.Crypto;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Configuration
@EnableScheduling
@EnableAsync
public class CryptoMarket {
    private CryptocurrencieRepository repository;
    private EmailService emailService;
    static Boolean changeMarket = false;

    private Crypto crypto;
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
        URL url = new URL("https://api.finearz.net/api/v1/market/?");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }

            String reasonString = response.toString();
            getSymbols(getCrypto(reasonString));
            getPrice(reasonString);

            if (giveAllCryptos == true) {
                cryptos = repository.saveAll(cryptos);
                giveAllCryptos = false;
            }
        }
    }


    public void getPrice(String responseBody) throws JSONException {

        JSONArray forLastPrices = new JSONArray(getMarketInfo(responseBody));
        String lastSymbol = "";

        for (int i = 0; i < forLastPrices.length(); i++) {
            JSONObject forLastPriceJson = forLastPrices.getJSONObject(i);

            if (giveAllCryptos == true) {
                if (cryptos.get(i).getSymbol().equals(lastSymbol)) {
                    setMarket("USDT", i, forLastPriceJson);

                } else {
                    setMarket("IRT", i, forLastPriceJson);
                }
                lastSymbol = cryptos.get(i).getSymbol();
            }
            float changePercent = ((Float.valueOf(forLastPriceJson.getString("lastPrice")) - lastChangesTaken.get(i)) * 100)
                    / lastChangesTaken.get(i);

            if (changePercent >= 0.005 || changePercent <= -0.005) {
                System.out.println("new price: " + (Float.valueOf(forLastPriceJson.getString("lastPrice"))));
                System.out.println("last price: " + lastChangesTaken.get(i));
                changeMarket = true;

                lastChangesTaken.set(i, Float.valueOf(forLastPriceJson.getString("lastDayChange")));

                crypto = repository.findById((long) (i + 1)).get();

                crypto.setLastDayChange(Float.valueOf(forLastPriceJson.getString("lastDayChange")));

                crypto.setPrice(Float.valueOf(forLastPriceJson.getString("lastPrice")));

                repository.save(crypto);

                emailService.sendEmail(
                        crypto.getName() + " change around " + changePercent + "%");

            }
        }
    }

    public void setMarket(String fiat, int i, JSONObject forLastPriceJson) throws JSONException {
        cryptos.get(i).setPrice(Float.valueOf(forLastPriceJson.getString("lastPrice")));
        cryptos.get(i).setLastDayChange(Float.valueOf(forLastPriceJson.getString("lastDayChange")));
        cryptos.get(i).setFiat(fiat);
        lastChangesTaken.add(cryptos.get(i).getPrice());
    }


    public static String getCrypto(String responseBody) throws JSONException {
        JSONArray albums = new JSONArray(responseBody);
        String symbol = null;
        String values = null;
        for (int i = 0; i < albums.length(); i++) {
            JSONObject album = albums.getJSONObject(i);
            symbol = album.getString("market");
            if (i == 0) {
                values = "[" + symbol + ",";
            } else if (i == albums.length() - 1) {
                values = values + symbol + "]";
            } else {
                values = values + symbol + ",";
            }
        }
        JSONArray forCryptos = new JSONArray(values);
        for (int i = 0; i < forCryptos.length(); i++) {
            JSONObject forCrypto = forCryptos.getJSONObject(i);
            symbol = forCrypto.getString("crypto");
            if (i == 0) {
                values = "[" + symbol + ",";
            } else if (i == albums.length() - 1) {
                values = values + symbol + "]";
            } else {
                values = values + symbol + ",";
            }
        }
        return values;
    }

    public String getMarketInfo(String response) throws JSONException {
        JSONArray albums = new JSONArray(response);
        String value = null;
        String values = null;
        for (int i = 0; i < albums.length(); i++) {
            JSONObject album = albums.getJSONObject(i);
            value = album.getString("marketInfo");
            if (i == 0) {
                values = "[" + value + ",";
            } else if (i == albums.length() - 1) {
                values = values + value + "]";
            } else {
                values = values + value + ",";
            }
        }
        return values;
    }


    public void getSymbols(String responseBody) throws JSONException {
        String symbol = null;
        String name = null;
        JSONArray forsymbols = new JSONArray(responseBody);
        for (int i = 0; i < forsymbols.length(); i++) {
            JSONObject forsymbol = forsymbols.getJSONObject(i);
            Crypto crypto = new Crypto();

            symbol = forsymbol.getString("symbol");
            crypto.setSymbol(symbol);

            name = forsymbol.getString("name");
            crypto.setName(name);

            if (giveAllCryptos == true) {
                cryptos.add(crypto);
            }
        }
    }

}
