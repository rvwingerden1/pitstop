package com.example.app.pitstop.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class GeoLocation {
    @NotNull BigDecimal latitude;
    @NotNull BigDecimal longitude;
    String name;
    BigDecimal altitude;
    BigDecimal accuracy;
    BigDecimal altitudeAccuracy;
    BigDecimal heading;
    BigDecimal speed;
}
