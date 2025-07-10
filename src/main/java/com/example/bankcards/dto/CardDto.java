package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDto {
    @Schema(description = "Card ID", example = "100")
    private Long id;
    @Schema(description = "Card number", example = "1234-5678-9012-3456")
    private String number;
    @Schema(description = "Card holder name", example = "John Doe")
    private String holderName;
    @Schema(description = "Expiration date", example = "2025-12-31")
    private LocalDate expirationDate;
    @Schema(description = "Card status", example = "Active")
    private CardStatus status;
    @Schema(description = "Card balance", example = "1000.00")
    private BigDecimal balance;
}
