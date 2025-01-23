package com.example.app.pitstop.handler;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.query.FindIncidents;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncidentQueryHandler {
    @HandleQuery
    List<Incident> handle(FindIncidents query) {
        return FluxCapacitor.search("incidents").fetchAll();
    }
}
