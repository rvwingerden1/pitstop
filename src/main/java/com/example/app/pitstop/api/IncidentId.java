package com.example.app.pitstop.api;

import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.Id;

public class IncidentId extends Id<Incident> {
    public static IncidentId newValue() {
        return new IncidentId(FluxCapacitor.generateId());
    }

    public IncidentId(String id) { super(id);}
}
