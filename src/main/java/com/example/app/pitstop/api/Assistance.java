package com.example.app.pitstop.api;

import lombok.Value;

import java.time.Instant;

@Value
public class Assistance {
    boolean resolved;
    Instant eta, ata;
    String assistantName;
}
