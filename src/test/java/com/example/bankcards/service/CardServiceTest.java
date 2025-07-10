package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardFilter;
import com.example.bankcards.util.CardNumberEncoder;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mappers.CardMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class CardServiceTest {
    @InjectMocks
    private CardServiceImpl cardService;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardNumberGenerator cardNumberGenerator;
    @Mock
    private CardNumberEncoder cardNumberEncoder;
    @Mock
    private CardMapper cardMapper;

    @Test
    @DisplayName("save - success")
    void save_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        String rawNumber = "1234567890123456";
        String encodedNumber = "ENCODED";
        Card card = new Card(encodedNumber, user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.ZERO);
        Card savedCard = new Card(1L, encodedNumber, user, card.getExpirationDate(), CardStatus.Active, BigDecimal.ZERO);
        CardDto cardDto = new CardDto(1L, "MASKED", "user1", card.getExpirationDate(), CardStatus.Active, BigDecimal.ZERO);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardNumberGenerator.generateCardNumber()).thenReturn(rawNumber);
        when(cardNumberEncoder.encode(rawNumber)).thenReturn(encodedNumber);
        when(cardRepository.existsByNumber(encodedNumber)).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(cardDto);
        CardDto result = cardService.save(1L);
        assertEquals(cardDto, result);
    }

    @Test
    @DisplayName("save - user not found")
    void save_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> cardService.save(2L));
    }

    @Test
    @DisplayName("deleteById - success")
    void deleteById_Success() {
        doNothing().when(cardRepository).deleteById(1L);
        assertDoesNotThrow(() -> cardService.deleteById(1L));
    }

    @Test
    @DisplayName("deleteAll - success")
    void deleteAll_Success() {
        doNothing().when(cardRepository).deleteAll();
        assertDoesNotThrow(() -> cardService.deleteAll());
    }

    @Test
    @DisplayName("update - success")
    void update_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card card = new Card(1L, "ENCODED", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        CardDto cardDto = new CardDto(1L, "MASKED", "user1", card.getExpirationDate(), CardStatus.Active, BigDecimal.valueOf(100));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);
        CardDto result = cardService.update(1L, null, card.getExpirationDate(), CardStatus.Active, BigDecimal.valueOf(100));
        assertEquals(cardDto, result);
    }

    @Test
    @DisplayName("update - card not found")
    void update_CardNotFound() {
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.update(2L, null, LocalDate.now(), CardStatus.Active, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("getById - success")
    void getById_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card card = new Card(1L, "ENCODED", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        CardDto cardDto = new CardDto(1L, "MASKED", "user1", card.getExpirationDate(), CardStatus.Active, BigDecimal.valueOf(100));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDto);
        CardDto result = cardService.getById(1L);
        assertEquals(cardDto, result);
    }

    @Test
    @DisplayName("getById - not found")
    void getById_NotFound() {
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getById(2L));
    }

    @Test
    @DisplayName("getAll - success")
    void getAll_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card card = new Card(1L, "ENCODED", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        CardDto cardDto = new CardDto(1L, "MASKED", "user1", card.getExpirationDate(), CardStatus.Active, BigDecimal.valueOf(100));
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(cardMapper.toDto(card)).thenReturn(cardDto);
        List<CardDto> result = cardService.getAll(new CardFilter(null, null, null, null), Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(cardDto, result.get(0));
    }

    @Test
    @DisplayName("getCardsByHolderName - success")
    void getCardsByHolderName_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card card = new Card(1L, "ENCODED", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        CardDto cardDto = new CardDto(1L, "MASKED", "user1", card.getExpirationDate(), CardStatus.Active, BigDecimal.valueOf(100));
        Page<Card> page = new PageImpl<>(List.of(card));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(cardMapper.toDto(card)).thenReturn(cardDto);
        List<CardDto> result = cardService.getCardsByHolderName("user1", new CardFilter(null, null, null, null), Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(cardDto, result.get(0));
    }

    @Test
    @DisplayName("getCardsByHolderName - user not found")
    void getCardsByHolderName_UserNotFound() {
        when(userRepository.findByUsername("not_found")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> cardService.getCardsByHolderName("not_found", mock(CardFilter.class), Pageable.unpaged()));
    }

    @Test
    @DisplayName("transferBetweenCards - success")
    void transferBetweenCards_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card fromCard = new Card(1L, "ENCODED1", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(200));
        Card toCard = new Card(2L, "ENCODED2", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenReturn(fromCard).thenReturn(toCard);
        assertDoesNotThrow(() -> cardService.transferBetweenCards("user1", 1L, 2L, BigDecimal.valueOf(50)));
        assertEquals(BigDecimal.valueOf(150), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(150), toCard.getBalance());
    }

    @Test
    @DisplayName("transferBetweenCards - not enough balance")
    void transferBetweenCards_NotEnoughBalance() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card fromCard = new Card(1L, "ENCODED1", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(10));
        Card toCard = new Card(2L, "ENCODED2", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        assertThrows(CardException.class, () -> cardService.transferBetweenCards("user1", 1L, 2L, BigDecimal.valueOf(50)));
    }

    @Test
    @DisplayName("blockCard - success")
    void blockCard_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card card = new Card(1L, "ENCODED", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        assertDoesNotThrow(() -> cardService.blockCard("user1", 1L));
        assertEquals(CardStatus.Blocked, card.getStatus());
    }

    @Test
    @DisplayName("blockCard - permission denied")
    void blockCard_PermissionDenied() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        User other = new User(); other.setId(2L); other.setUsername("other");
        Card card = new Card(1L, "ENCODED", other, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        assertThrows(PermissionNotAllowed.class, () -> cardService.blockCard("user1", 1L));
    }

    @Test
    @DisplayName("getBalance - success")
    void getBalance_Success() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        Card card = new Card(1L, "ENCODED", user, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        BigDecimal result = cardService.getBalance("user1", 1L);
        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    @DisplayName("getBalance - permission denied")
    void getBalance_PermissionDenied() {
        User user = new User(); user.setId(1L); user.setUsername("user1");
        User other = new User(); other.setId(2L); other.setUsername("other");
        Card card = new Card(1L, "ENCODED", other, LocalDate.now().plusYears(3), CardStatus.Active, BigDecimal.valueOf(100));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        assertThrows(PermissionNotAllowed.class, () -> cardService.getBalance("user1", 1L));
    }
}
