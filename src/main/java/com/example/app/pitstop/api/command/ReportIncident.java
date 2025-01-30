package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;
import lombok.Value;

import static java.lang.String.format;

@Value
@Builder(toBuilder = true)
public class ReportIncident implements CreateIncidentCommand {
    IncidentId incidentId;
    IncidentDetails details;

    @Apply
    Incident apply(Message message) {
        return Incident.builder()
                .incidentId(incidentId)
                .details(details)
                .start(message.getTimestamp())
                .build();
    }
}
