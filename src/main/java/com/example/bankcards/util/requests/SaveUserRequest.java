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
public class SaveUserRequest {
    @Schema(description = "Username", example = "admin")
    @NotBlank(message = "User name cannot be blank")
    private String userName;
    @Schema(description = "Password", example = "password123")
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @Schema(description = "Roles", example = "[USER, ADMIN]")
    @NotBlank(message = "Role cannot be blank")
    private Set<String> roles;
}
