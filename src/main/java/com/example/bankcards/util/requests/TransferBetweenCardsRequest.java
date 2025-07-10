package com.example.bankcards.util.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransferBetweenCardsRequest {
    @Schema(description = "Source card ID", example = "100")
    @NotBlank(message = "From cardId cannot be blank")
    private Long fromCardId;
    @Schema(description = "Destination card ID", example = "101")
    @NotBlank(message = "To cardId cannot be blank")
    private Long toCardId;
    @Schema(description = "Transfer amount", example = "250.00")
    @NotBlank(message = "Amount cannot be blank")
    private BigDecimal amount;
}
