package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardFilter;
import com.example.bankcards.util.CardNumberEncoder;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mappers.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final CardNumberEncoder cardNumberEncoder;
    private final CardMapper cardMapper;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository, CardNumberGenerator cardNumberGenerator, CardNumberEncoder cardNumberEncoder, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.cardNumberEncoder = cardNumberEncoder;
        this.cardMapper = cardMapper;
    }

    public CardDto save(Long id) {
        User holder = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id " + id + "not found")
        );
        String cardNumber;
        do {
            cardNumber = cardNumberEncoder.encode(cardNumberGenerator.generateCardNumber());
        } while (cardRepository.existsByNumber(cardNumber));
        Card card = new Card(cardNumber, holder, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(0));
        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }

    public void deleteById(Long id) {
        cardRepository.deleteById(id);
    }

    public void deleteAll() {
        cardRepository.deleteAll();
    }

    public CardDto update(Long id, Long newHolderId, LocalDate newExpirationDate, CardStatus newStatus, BigDecimal newBalance) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException("Card with id " + id + " not found")
        );
        if (newHolderId != null) {
            User holder = userRepository.findById(newHolderId).orElseThrow(
                    () -> new UserNotFoundException("User with id " + newHolderId + "not found")
            );
            card.setHolder(holder);
        }

        if (card.getExpirationDate().isBefore(newExpirationDate)) {
            throw new EntityUpdateException("New expiration date cannot be before old expiration date");
        }
        if (card.getBalance().compareTo(newBalance) < 0) {
            throw new EntityUpdateException("New balance cannot be negative");
        }
        Optional.ofNullable(newExpirationDate)
                .ifPresent(card::setExpirationDate);

        Optional.ofNullable(newStatus)
                .ifPresent(card::setStatus);

        Optional.ofNullable(newBalance)
                .ifPresent(card::setBalance);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }

    public CardDto getById(Long id) {
        return cardRepository.findById(id).map(cardMapper::toDto).orElseThrow(
                () -> new CardNotFoundException("Card with id " + id + " not found")
        );
    }

    public List<CardDto> getAll(CardFilter filter, Pageable pageable) {
        Page<Card> cats = cardRepository.findAll(filter.toSpecification(), pageable);
        return cats.stream()
                .map(cardMapper::toDto)
                .toList();
    }

    public List<CardDto> getCardsByHolderName(String holderName, CardFilter filter, Pageable pageable) {
        User holder = userRepository.findByUsername(holderName).orElseThrow(
                () -> new UserNotFoundException("User with name " + holderName + "not found")
        );
        Page<Card> cards = cardRepository.findAll(filter.toSpecification().and((root, q, cb) ->
                cb.equal(root.get("holder").get("id"), holder.getId())), pageable);
        System.out.println(cards.getTotalElements());
        return cards.stream()
                .map(cardMapper::toDto)
                .toList();
    }

    public void transferBetweenCards(String holderName, Long fromCardId, Long toCardId, BigDecimal amount) {
        if (fromCardId.equals(toCardId)) {
            throw new CardException("Cannot transfer between same cards");
        }
        User holder = userRepository.findByUsername(holderName).orElseThrow(
                () -> new UserNotFoundException("User with name " + holderName + "not found")
        );
        Card fromCard = cardRepository.findById(fromCardId).orElseThrow(() -> new CardNotFoundException("Card with id " + fromCardId + " not found"));
        Card toCard = cardRepository.findById(toCardId).orElseThrow(() -> new CardNotFoundException("Card with id " + toCardId + " not found"));
        if (!Objects.equals(fromCard.getHolder().getId(), holder.getId())) {
            throw new PermissionNotAllowed(fromCard.getId());
        }
        if (!Objects.equals(toCard.getHolder().getId(), holder.getId())) {
            throw new PermissionNotAllowed(toCard.getId());
        }
        if (fromCard.getStatus() != CardStatus.Active) {
            throw new CardException("From card is not active");
        }

        if (toCard.getStatus() != CardStatus.Active) {
            throw new CardException("To card is not active");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new CardException("Not enough balance to transfer from card:" + fromCard.getId());
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    public void blockCard(String holderName, Long cardId) {
        User holder = userRepository.findByUsername(holderName).orElseThrow(
                () -> new UserNotFoundException("Holder with name " + holderName + "not found")
        );
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card with id " + cardId + " not found"));
        if (!Objects.equals(card.getHolder().getId(), holder.getId())) {
            throw new PermissionNotAllowed(card.getId());
        }
        card.setStatus(CardStatus.Blocked);
        cardRepository.save(card);
    }

    public BigDecimal getBalance(String holderName, Long cardId) {
        User holder = userRepository.findByUsername(holderName).orElseThrow(
                () -> new UserNotFoundException("Holder with username " + holderName + "not found")
        );
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card with id " + cardId + " not found"));
        if (!Objects.equals(card.getHolder().getId(), holder.getId())) {
            throw new PermissionNotAllowed(card.getId());
        }
        return card.getBalance();
    }
}
