package com.example.crypto.service;

import com.example.crypto.model.Cryptocurrencie;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Configuration
public class CryptoConfig extends Thread {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    static private CryptocurrencieRepository repository ;


    @Override
    public void run() {
        if (LocalTime.now().getHour() == 20) {
            giveAllCryptos = false;
        }
        while (true) {
            System.out.println("Hello!");
            try {
                System.out.println(cryptos);
                System.out.println("cryptoStatusChange: " + cryptoStatusChange);
                System.out.println("giveAllCryptos: " + giveAllCryptos);
                getCryptocurrencieIRT();
                getCryptocurrencieUSDT();
                if (cryptoStatusChange == true) {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom("pouryakarimi1382@gmail.com");
                    message.setTo("finearztest@gmail.com");
                    message.setText("change in market!");
                    message.setSubject(stringForEmail());
                    mailSender.send(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    static List<Cryptocurrencie> cryptos = new ArrayList<>();
    Cryptocurrencie cryptocurrencie;
    HashMap<String, Float> nameAndChange = new HashMap<>();
    static Boolean cryptoStatusChange = false;
    static Boolean  giveAllCryptos = false;

    @Bean
    public void getCryptocurrencieIRT() throws IOException, JSONException {
        URL url = new URL("https://api.finearz.net/api/v1/market/?fiat=IRT");
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
            getMarketInfoIRT(reasonString);
            stringForEmail();

        }
    }


    @Bean
    public void getCryptocurrencieUSDT() throws IOException, JSONException {
        StringBuilder responseUSDT = new StringBuilder();
        URL url = new URL("https://api.finearz.net/api/v1/market/?fiat=USDT");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseUSDT.append(responseLine);
            }
        }
        String reasonString = responseUSDT.toString();
        if (!cryptos.isEmpty()) {
            getMarketInfoUSDT(reasonString);
        }
        if (giveAllCryptos == false) {
            repository.saveAll(cryptos);
            giveAllCryptos = true;
        }

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

    public void getSymbols(String responseBody) throws JSONException {
        String symbol = null;
        String name = null;
        JSONArray forsymbols = new JSONArray(responseBody);
        for (int i = 0; i < forsymbols.length(); i++) {
            JSONObject forsymbol = forsymbols.getJSONObject(i);
            Cryptocurrencie cryptocurrencie = new Cryptocurrencie();
            symbol = forsymbol.getString("symbol");
            cryptocurrencie.setSymbol(symbol);
            name = forsymbol.getString("name");
            cryptocurrencie.setName(name);
            cryptos.add(cryptocurrencie);
        }


    }

    public void getMarketInfoIRT(String responseBody) throws JSONException {
        System.out.println("cryptos: "+cryptos);
        JSONArray albums = new JSONArray(responseBody);
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
        JSONArray forLastPrices = new JSONArray(values);
        for (int i = 0; i < forLastPrices.length(); i++) {
            JSONObject forLastPrice = forLastPrices.getJSONObject(i);
            float newPrice = Float.valueOf(forLastPrice.getString("lastPrice"));
            if (giveAllCryptos == false) {
                cryptos.get(i).setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
                cryptos.get(i).setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
            } else if (Float.valueOf(forLastPrice.getString("lastDayChange")) >= 2 || Float.valueOf(forLastPrice.getString("lastDayChange")) <= -2) {
                cryptocurrencie = repository.findById((long) (i+1)).get();
                cryptocurrencie.setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
                cryptocurrencie.setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
                System.out.println("save repository: IRT");
                repository.save(cryptocurrencie);
                nameAndChange.put(cryptocurrencie.getName(), cryptocurrencie.getLastDayChange());
                cryptoStatusChange = true;
                System.out.println("changing...");
            } else {
                cryptoStatusChange = false;
            }
        }

    }

    public void getMarketInfoUSDT(String responseBody) throws JSONException {
        int j = 0;
        JSONArray albums = new JSONArray(responseBody);
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
        JSONArray forLastPricesAndChange = new JSONArray(values);
        System.out.println("cryptos : " + cryptos);
        for (int i = 0; i < forLastPricesAndChange.length(); i++) {
            JSONObject forLastPrice = forLastPricesAndChange.getJSONObject(i);
            if (giveAllCryptos == false) {
                if (j == 8) {
                    j++;
                    i--;
                    continue;
                }
                cryptos.get(j).setPriceUSDT(Float.valueOf(forLastPrice.getString("lastPrice")));
                j++;
            } else if (cryptoStatusChange && i != 8) {
                repository.findById((long) (i + 1)).get().setPriceUSDT(Float.valueOf(forLastPrice.getString("lastPrice")));
                nameAndChange.put(repository.findById((long) (i + 1)).get().getName(), Float.valueOf(forLastPrice.getString("lastPrice")));
            }

        }
    }

    private String stringForEmail() {
        String stringEmail = "";

        for (String i : nameAndChange.keySet()) {
            stringEmail += i + "change around " + nameAndChange.get(i) + "persent";
        }

        return stringEmail;
    }
}



