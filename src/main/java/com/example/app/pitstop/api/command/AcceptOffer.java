package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.Offer;
import com.example.app.pitstop.api.OfferId;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AcceptOffer implements UpdateIncidentCommand {
    @NotNull
    IncidentId incidentId;
    OfferId offerId;

    @Apply
    Offer apply(Offer offer) {
        return offer.toBuilder().accepted(true).build();
    }
}
