package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.publishing.routing.RoutingKey;

public interface IncidentCommand {
    @RoutingKey
    IncidentId getIncidentId();
}
