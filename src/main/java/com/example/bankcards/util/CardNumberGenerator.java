package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CardNumberGenerator {
    public String generateCardNumber() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }
}
