package com.example.crypto.service;

import com.example.crypto.model.Cryptocurrencie;
import com.example.crypto.repository.CryptocurrencieRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CryptoChanges extends Thread {
    private CryptocurrencieRepository repository;

    public CryptoChanges(CryptocurrencieRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        while (true) {
            try {
                getCryptocurrencieIRT();
                CryptoChanges.sleep(2000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    List<Cryptocurrencie> cryptos = new ArrayList<>();
    Cryptocurrencie cryptocurrencie;
    HashMap<String, Float> nameAndChange = new HashMap<>();
    static Boolean giveAllCryptos = true;

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
            System.out.println("giveAllCryptos: " + giveAllCryptos);
            System.out.println("repository: " + repository.findAll());
            getSymbols(getCrypto(reasonString));
            getMarketInfoIRT(reasonString);
            if (giveAllCryptos == true)
                cryptos = repository.saveAll(cryptos);
            giveAllCryptos = false;
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
            if (giveAllCryptos == true) {
                cryptos.add(cryptocurrencie);

            }
        }


    }

    public void getMarketInfoIRT(String responseBody) throws JSONException {
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
        String lastSymbol = "";

        for (int i = 0; i < forLastPrices.length(); i++) {
            JSONObject forLastPrice = forLastPrices.getJSONObject(i);
            float newPrice = Float.valueOf(forLastPrice.getString("lastPrice"));
            if (giveAllCryptos == true) {
                if (cryptos.get(i).getSymbol().equals(lastSymbol)) {
                    cryptos.get(i).setPriceUSDT(Float.valueOf(forLastPrice.getString("lastPrice")));
                    cryptos.get(i).setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));

                } else {
                    cryptos.get(i).setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
                    cryptos.get(i).setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
                }
                lastSymbol = cryptos.get(i).getSymbol();

            } else if (Float.valueOf(forLastPrice.getString("lastDayChange")) >= 2 || Float.valueOf(forLastPrice.getString("lastDayChange")) <= -2) {
                System.out.println("we are in changing...");
                cryptocurrencie = repository.findById((long) (i + 1)).get();
                cryptocurrencie.setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
                cryptocurrencie.setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
                repository.save(cryptocurrencie);
                nameAndChange.put(cryptocurrencie.getName(), cryptocurrencie.getLastDayChange());
            }
//            if (giveAllCryptos == false) {
//            } else if (Float.valueOf(forLastPrice.getString("lastDayChange")) >= 2 || Float.valueOf(forLastPrice.getString("lastDayChange")) <= -2) {
//                cryptocurrencie = repository.findById((long) (i + 1)).get();
//                cryptocurrencie.setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
//                cryptocurrencie.setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
//                System.out.println("save repository: IRT");
//                repository.save(cryptocurrencie);
//                nameAndChange.put(cryptocurrencie.getName(), cryptocurrencie.getLastDayChange());
//                cryptoStatusChange = true;
//                System.out.println("changing...");
//            } else {
//                cryptoStatusChange = false;
//            }


        }
    }
}
