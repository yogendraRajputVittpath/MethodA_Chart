package com.CandleData.service.kite;

import com.CandleData.config.kite.KiteConfig;
import com.CandleData.entity.kite.KiteSession;
import com.CandleData.repository.kite.KiteSessionRepository;
import com.CandleData.service.util.Encryptor;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class KiteService {

    private final KiteConfig kiteConfig;
    private final KiteSessionRepository sessionRepository;
    private KiteConnect kiteConnect;
    private final Encryptor encryptor;
    @Value("${kite.api.secret}")
    private String secret;
    private String decryptedSecret;
   
    @Value("${kite.api.key}")
    private String encryptedApiKey;  
    private String decryptedApiKey;
    @PostConstruct
    public void init() {
        this.decryptedSecret = encryptor.decrypt(secret);
        this.decryptedApiKey = encryptor.decrypt(encryptedApiKey);

        if (decryptedSecret == null || decryptedApiKey == null) {
            throw new RuntimeException("Decryption failed! Check secretKey/initVector or encrypted values");
        }

        log.info("Kite credentials decrypted successfully");
    }

    public String getLoginUrl() {
        kiteConnect = new KiteConnect(this.decryptedApiKey);
        kiteConnect.setUserId(kiteConfig.getUserId());
        return kiteConnect.getLoginURL();
    }

    public void generateSession(String requestToken) throws IOException, KiteException {
        KiteConnect client = new KiteConnect(this.decryptedApiKey);
        client.setUserId(kiteConfig.getUserId());

        User user = client.generateSession(requestToken, this.decryptedSecret);
        log.info("Decrypted API Key Used: {}", this.decryptedApiKey);
        log.info("Decrypted API Secret Used: {}", this.decryptedSecret);
       
        KiteSession session = KiteSession.builder()
                .userId(kiteConfig.getUserId())
                .accessToken(encryptor.encrypt(user.accessToken))
                .publicToken(encryptor.encrypt(user.publicToken))
                .loginTime(LocalDateTime.now())
                .build();
        
        sessionRepository.save(session);
        
        client.setAccessToken(user.accessToken);
        this.kiteConnect = client;
        log.info("Session generated and saved for user: {}", user.userId);
    }

    public KiteConnect getKiteConnect() {
        if (this.kiteConnect != null) {
            return this.kiteConnect;
        }

        return sessionRepository.findFirstByOrderByLoginTimeDesc()
                .map(session -> {
                	KiteConnect client = new KiteConnect(this.decryptedApiKey);
                    client.setAccessToken(encryptor.decrypt(session.getAccessToken()));
                    client.setUserId(session.getUserId());
                    this.kiteConnect = client;
                    return client;
                })
                .orElseThrow(() -> new RuntimeException("No active session found. Please login via /api/kite/login"));
    }

    public String getApiKey() {
        log.info("API Key used in getApiKey: {}", this.decryptedApiKey);
    	return this.decryptedApiKey;
    }
}