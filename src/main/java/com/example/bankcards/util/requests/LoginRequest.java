package com.example.bankcards.util.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoginRequest{
    @NotBlank(message = "UserName cannot be blank")
    @Schema(description = "User name", example = "masha")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "User password", example = "5252525252")
    private String password;
}
