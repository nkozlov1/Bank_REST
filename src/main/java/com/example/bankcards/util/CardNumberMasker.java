package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CardNumberMasker {
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            throw new IllegalArgumentException("Invalid card number");
        }
        return "**** **** **** ".concat(cardNumber.substring(cardNumber.length() - 4));
    }
}
