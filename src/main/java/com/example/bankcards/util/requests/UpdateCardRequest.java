package com.example.bankcards.util.requests;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UpdateCardRequest {
    @Schema(description = "Card ID", example = "100")
    @NotBlank(message = "Id cannot be blank")
    private Long id;
    @Schema(description = "New holder ID", example = "2")
    private Long newHolderId;
    @Schema(description = "New expiration date", example = "2026-01-01")
    private LocalDate newExpirationDate;
    @Schema(description = "New card status", example = "Blocked")
    private CardStatus newStatus;
    @Schema(description = "New card balance", example = "500.00")
    private BigDecimal newBalance;
}
