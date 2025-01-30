package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;

import static java.lang.String.format;

public interface CreateIncidentCommand extends IncidentCommand {
    @AssertLegal
    default void assertLegal(Incident incident) {
        throw new IllegalCommandException(format("Incident with id {%s} already exists.", getIncidentId()));
    }
}
