package com.CandleData.service.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EncryptorRunner implements CommandLineRunner {

    private final Encryptor encryptor;

    @Override
    public void run(String... args) {
        System.out.println("API KEY  = " + encryptor.encrypt("az3ygvlusidhkzv3"));
        System.out.println("SECRET   = " + encryptor.encrypt("3juu80yxrlwhaqu7jorgc07pmyawokdr"));
    }
}