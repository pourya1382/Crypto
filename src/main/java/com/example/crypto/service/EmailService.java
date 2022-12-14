package com.example.crypto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    public void sendEmail(String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pouryakarimi1382@gmail.com");
        message.setTo("finearztest@gmail.com");
        message.setText(text);
        message.setSubject("change in market!");
        mailSender.send(message);
    }


}
