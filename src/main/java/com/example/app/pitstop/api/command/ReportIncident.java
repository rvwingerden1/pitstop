package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.handler.IncidentCommand;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;
import lombok.Value;

import static java.lang.String.format;

@Value
@Builder(toBuilder = true)
public class ReportIncident implements IncidentCommand {
    IncidentId incidentId;
    IncidentDetails details;

    @AssertLegal
    void assertLegal(Incident incident) {
        throw new IllegalArgumentException(format("Incident with id {%s} already exists.", incidentId));
    }

    @Apply
    Incident apply(Message message) {
        return Incident.builder()
                .incidentId(incidentId)
                .details(details)
                .start(message.getTimestamp())
                .build();
    }
}
