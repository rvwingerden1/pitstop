package com.example.app.pitstop.handler;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.command.IncidentCommand;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import org.springframework.stereotype.Component;

@Component
public class IncidentCommandHandler {
    @HandleCommand
    void handle(IncidentCommand command) {
        FluxCapacitor.loadAggregate(command.getIncidentId(), Incident.class)
                .assertAndApply(command);
    }
//    @HandleCommand
//    void handle(AcceptOffer command) {
//        FluxCapacitor.loadAggregateFor(command.getOfferId(), Offer.class)
//                .assertAndApply(command);
//    }
//    @HandleCommand
//    void handle(AcceptOffer command) {
//        FluxCapacitor.loadEntity(command.getOfferId())
//                .assertAndApply(command);
//    }
}
