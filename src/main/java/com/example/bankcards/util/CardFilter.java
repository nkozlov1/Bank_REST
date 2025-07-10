package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardFilter(String number, LocalDate expirationDate, CardStatus status, BigDecimal balance) {
    public Specification<Card> toSpecification() {
        return Specification.where(numberSpec())
                .and(expirationDateSpec())
                .and(statusSpec())
                .and(balanceSpec());
    }

    private Specification<Card> numberSpec() {
        return ((root, query, cb) -> StringUtils.hasText(number)
                ? cb.like(root.get("number"), "%" + number + "%")
                : null);
    }

    private Specification<Card> expirationDateSpec() {
        return ((root, query, cb) -> expirationDate != null
                ? cb.like(root.get("expirationDate"), "%" + expirationDate + "%")
                : null);
    }

    private Specification<Card> statusSpec() {
        return ((root, query, cb) -> status != null
                ? cb.like(root.get("status"), "%" + status + "%")
                : null);
    }

    private Specification<Card> balanceSpec() {
        return ((root, query, cb) -> balance != null
                ? cb.like(root.get("balance"), "%" + balance + "%")
                : null);
    }
}
