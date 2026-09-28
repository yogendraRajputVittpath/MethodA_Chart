package com.CandleData.config;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.CandleData.service.util.Encryptor;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);
    private final Encryptor encryptor;
    
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String encryptedMailPassword;

    @Bean
    public JavaMailSender javaMailSender() {
        log.info("Initializing JavaMailSender with decrypted mail password...");

        // Use Encryptor utility for decryption
        String decryptedPassword = encryptor.decrypt(encryptedMailPassword);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(decryptedPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        log.info("JavaMailSender configured successfully.");
        return mailSender;
    }
}

