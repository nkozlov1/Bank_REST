package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "User password", example = "password123")
    private String password;
    @Schema(description = "User roles", example = "[\"ADMIN\", \"USER\"]")
    private Set<String> roles;
}
