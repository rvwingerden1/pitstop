package com.example.app.pitstop;

import com.example.app.pitstop.api.command.AcceptOffer;
import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.ReportIncident;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class IncidentLifeCycleHandler {
    public static final Duration INCIDENT_CLOSING_TIME = Duration.ofHours(24);
    public static final Duration ESCALATE = Duration.ofMinutes(30);

    @HandleEvent
    public void handle(ReportIncident event) {
        FluxCapacitor.scheduleCommand(EscalateIncident.builder().incidentId(event.getIncidentId()).build(),
                "escalate-" + event.getIncidentId().getFunctionalId(), ESCALATE);

        FluxCapacitor.scheduleCommand(CloseIncident.builder().incidentId(event.getIncidentId()).build(),
                "deadline-" + event.getIncidentId().getFunctionalId(), INCIDENT_CLOSING_TIME);
    }

    @HandleEvent
    public void handle(AcceptOffer event) {
        FluxCapacitor.cancelSchedule("deadline-" + event.getIncidentId().getFunctionalId());
    }
}
