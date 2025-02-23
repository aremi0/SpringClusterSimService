package com.aremi.springclustersimservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
