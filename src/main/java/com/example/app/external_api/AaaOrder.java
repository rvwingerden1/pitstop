package com.example.app.external_api;

import com.example.app.pitstop.api.Incident;
import com.example.app.user.api.UserProfile;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AaaOrder {
    @JsonAlias("lat")
    @NotNull
    BigDecimal latitude;
    @JsonAlias("lon")
    @NotNull
    BigDecimal longitude;
    @JsonAlias("destinationName")
    String destionationName;
    @JsonProperty("licensePlate")
    String licensePlate;
    @JsonAlias("driverName")
    String driverName;

    public static AaaOrder from(Incident incident, UserProfile user) {
        return AaaOrder.builder()
                .latitude(incident.getDetails().getLocation().getLatitude())
                .longitude(incident.getDetails().getLocation().getLongitude())
                .destionationName(incident.getDetails().getLocation().getName())
                .licensePlate(incident.getDetails().getVehicle().getLicensePlateNumber())
                .driverName("testing")
                .build();
    }
}
