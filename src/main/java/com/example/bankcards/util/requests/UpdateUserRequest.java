package com.example.bankcards.util.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UpdateUserRequest {
    @Schema(description = "User ID", example = "1")
    @NotBlank(message = "Id cannot be blank")
    private Long id;
    @Schema(description = "New password", example = "newpassword123")
    private String password;
    @Schema(description = "New roles", example = "[USER, ADMIN]")
    private Set<String> roles;
}
