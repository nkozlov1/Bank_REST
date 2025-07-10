package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Component
public class CardNumberEncoder {

    @Value("${encoder.secret}")
    private String secret;

    public String encode(String cardNumber) {
        Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(cardNumber.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Encode error", e);
        }
    }

    public String decrypt(String cardNumber) {
        Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cardNumber)));
        } catch (Exception e) {
            throw new IllegalStateException("Decrypt error", e);
        }
    }
}
