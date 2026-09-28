package com.CandleData.service.util;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;  

@Service
public class Encryptor  {

    private static final Logger log = LoggerFactory.getLogger(Encryptor.class);
    // Read these values from property File
    @Value("${security.encryptor.initVector}")
    private String initVector;
    
    @Value("${security.encryptor.secretKey}")
    private String secretKey;
  
//    public static void main(String[] args) throws Exception {
////     Encryptor e = new Encryptor();
//        String[] values = {
//            "dulm8mv8lq0zmko9" ,
//            "kka57hzpsst2ws7xiusj2o0hdk077i24",
//            "1234",
//            "@1234Asdf@",
//            "0g5iw80s89eu3xyy",
//            "g6pyh0n95bdc8a67p3kriurgd69fdbps",
//            "@MethodA#DB_042026@",
//            "@Info#Method#2026@",
//            "az3ygvlusidhkzv3",
//            "3juu80yxrlwhaqu7jorgc07pmyawokdr"
//            
//        };
//        List<String> list = new ArrayList<>();
//
//        for (String val : values) {
//            list.add(e.encrypt(val));
//        }
//    }
    
    public String encrypt(String decryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, secKey, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(decryptedValue.getBytes(StandardCharsets.UTF_8));
            String encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);

            log.info("'{}' Encrypted To -> '{}'", decryptedValue, encryptedValue);
            return encryptedValue;
        } catch (Exception e) {
            log.error("Error encrypting value: {}", decryptedValue, e);
            return null;
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            cipher.init(Cipher.DECRYPT_MODE, secKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            String decryptedValue = new String(decryptedBytes);

            log.info("'{}' Decrypted Successfully -> {}", encryptedValue, decryptedValue);
            return decryptedValue;
        } catch (Exception e) {
            log.error("Error decrypting value: {}", encryptedValue, e);
            return null;
        }
    }

	
}

