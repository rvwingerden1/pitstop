package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import jakarta.annotation.Nullable;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public interface UpdateIncidentCommand extends IncidentCommand {
    @AssertLegal
    default void assertLegal(@Nullable Incident incident) {
        ofNullable(incident).orElseThrow(() -> new IllegalCommandException(format("Incident with id {%s} does not " +
                "exist.", getIncidentId())));
    }
}
