package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardNumberEncoder;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.CardNumberMasker;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {
    private final CardNumberEncoder cardNumberEncoder;

    public CardDto toDto(Card card) {
        return new CardDto(card.getId(), CardNumberMasker.maskCardNumber(cardNumberEncoder.decrypt(card.getNumber())), card.getHolder().getUsername(), card.getExpirationDate(), card.getStatus(), card.getBalance());
    }
}
