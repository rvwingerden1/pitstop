package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ReportIncident implements CreateIncidentCommand {
    IncidentId incidentId;
    IncidentDetails details;

    @Apply
    Incident apply(Message message, Sender sender) {
        return Incident.builder()
                .incidentId(incidentId)
                .reporter(sender.getUserId())
                .details(details)
                .start(message.getTimestamp())
                .build();
    }
}
