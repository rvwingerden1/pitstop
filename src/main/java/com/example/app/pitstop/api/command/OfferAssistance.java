package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.Offer;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class OfferAssistance implements UpdateIncidentCommand {
    IncidentId incidentId;
    Offer offer;

    @Apply
    Incident apply(Incident incident) {
        return incident.toBuilder()
                .offer(offer)
                .build();
    }
}
