package com.example.app.refdata.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class OperatorDetails {
    @NotBlank String name;
}
