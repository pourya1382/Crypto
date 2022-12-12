package com.example.crypto.service;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;

@Service
public class CryptoChanges extends Thread {
    private CryptoConfig config;
    @Autowired
    private JavaMailSender mailSender;

    public CryptoChanges(CryptoConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        while (true) {
            if (LocalTime.now().getHour() == 20) {
                config.giveAllCryptos = false;
            }
            try {
                config.getCryptocurrencieIRT();
                System.out.println(config.changeMarket);
                if (config.changeMarket == true) {
                    String text = config.stringForEmail();
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom("pouryakarimi1382@gmail.com");
                    message.setTo("finearztest@gmail.com");
                    message.setText(text);
                    message.setSubject("change in market!");
                    mailSender.send(message);
                }
                CryptoChanges.sleep(20000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
