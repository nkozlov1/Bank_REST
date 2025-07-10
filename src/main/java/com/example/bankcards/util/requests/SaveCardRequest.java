package com.example.bankcards.util.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SaveCardRequest {
    @Schema(description = "Holder ID", example = "1")
    @NotBlank(message = "HolderId cannot be blank")
    private Long holderId;
}
