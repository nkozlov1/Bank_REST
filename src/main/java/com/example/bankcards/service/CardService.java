package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.util.CardFilter;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CardService {
    CardDto save(Long id);

    void deleteById(Long id);

    void deleteAll();

    CardDto update(Long id, Long newHolderId, LocalDate newExpirationDate, CardStatus newStatus, BigDecimal newBalance);

    CardDto getById(Long id);

    List<CardDto> getAll(CardFilter filter, Pageable pageable);

    List<CardDto> getCardsByHolderName(String holderName, CardFilter filter, Pageable pageable);

    void transferBetweenCards(String holderName, Long fromCardId, Long toCardId, BigDecimal amount);

    void blockCard(String holderName, Long cardId);

    BigDecimal getBalance(String holderName, Long cardId);
}
