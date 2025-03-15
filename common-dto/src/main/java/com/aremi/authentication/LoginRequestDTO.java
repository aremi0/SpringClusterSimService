package com.aremi.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@ToString
public class LoginRequestDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}