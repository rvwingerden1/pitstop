package com.example.app.pitstop.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class Vehicle {
    @NotBlank String licensePlateNumber;
    String make;
    String model;
    String year;
    String color;
}
