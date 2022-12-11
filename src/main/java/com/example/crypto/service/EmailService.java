package com.example.crypto.service;

import org.springframework.mail.SimpleMailMessage;

public class EmailService {
    SimpleMailMessage message = new SimpleMailMessage();

//    @Autowired
//    private JavaMailSender mailSender;
//    SimpleMailMessage message = new SimpleMailMessage();
//                    message.setFrom("pouryakarimi1382@gmail.com");
//                    message.setTo("finearztest@gmail.com");
//                    message.setText("change in market!");
//                    message.setSubject(stringForEmail());
//                    mailSender.send(message);
//private String stringForEmail() {
//    String stringEmail = "";
//
//    for (String i : nameAndChange.keySet()) {
//        stringEmail += i + "change around " + nameAndChange.get(i) + "persent";
//    }
//
//    return stringEmail;
//}
}
