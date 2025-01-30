package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import static java.util.Optional.ofNullable;

@Value
@Builder
public class CloseIncident implements UpdateIncidentCommand {
    @NotNull
    IncidentId incidentId;

    @AssertLegal
    void cannotBeClosedTwice(Incident incident) {
        ofNullable(incident.getEnd())
                .ifPresent(instant -> {
                    throw new IllegalCommandException("Already Closed");
                });
    }

    @AssertLegal
    void mustHaveOfferAccepted(Incident incident) {
        incident.getAcceptedOffer()
                .orElseThrow(() -> new IllegalCommandException("No accepted offer present!"));
    }

    @Apply
    Incident apply(Incident incident, Message message) {
        return incident.toBuilder()
                .end(message.getTimestamp()).build();
    }
}
