package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class EscalateIncident implements UpdateIncidentCommand {
    IncidentId incidentId;

    @AssertLegal
    void notAcceptedYet(Incident incident) {
        incident.getAcceptedOffer().ifPresent(offer -> {
            throw new IllegalCommandException("Offer already accepted, cannot escalate incident");
        });
    }

    @Apply
    Incident apply(Incident incident) {
        return incident.toBuilder()
                .escalated(true)
                .build();
    }
}
