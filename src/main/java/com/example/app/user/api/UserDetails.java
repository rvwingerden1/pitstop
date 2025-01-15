package com.example.app.user.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDetails {
    @NotBlank String firstName, lastName;
    @NotNull @Valid Email email;
    String phoneNumber;
}
