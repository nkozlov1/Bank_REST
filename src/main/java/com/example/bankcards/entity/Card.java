package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User holder;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance;

    public Card(String number, User holder, LocalDate expirationDate, CardStatus status, BigDecimal balance) {
        this.number = number;
        this.holder = holder;
        this.expirationDate = expirationDate;
        this.status = status;
        this.balance = balance;
    }

    public Card(Long id, String number, LocalDate expirationDate, CardStatus status, BigDecimal balance) {
        this.number = number;
        this.expirationDate = expirationDate;
        this.status = status;
        this.balance = balance;
    }
}
