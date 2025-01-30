package com.example.app.pitstop.api;

import com.example.app.user.api.UserId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.fluxcapacitor.javaclient.modeling.Aggregate;
import io.fluxcapacitor.javaclient.modeling.EntityId;
import io.fluxcapacitor.javaclient.modeling.Member;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Aggregate(collection = "incidents", searchable = true)
@Builder(toBuilder = true)
@Value
public class Incident {
    @EntityId
    IncidentId incidentId;
    IncidentDetails details;

    UserId reporter;

    Instant start, end;

    @Singular
    @Member
    List<Offer> offers;

    Assistance assistance;

    @JsonIgnore
    @SuppressWarnings("unused")
    public Optional<Offer> getAcceptedOffer() {
        return offers.stream().filter(Offer::isAccepted).findFirst();
    }
}
