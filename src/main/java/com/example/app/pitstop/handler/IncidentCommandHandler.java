package com.example.app.pitstop.handler;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.command.AcceptOffer;
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


    //For Members of Incident (e.g. Offer)
    @HandleCommand
    Object handle(AcceptOffer command) {
        return FluxCapacitor.loadEntity(command.getOfferId()).assertAndApply(command).get();
//        return FluxCapacitor.loadAggregateFor(command.getOfferId(), Offer.class)
//                .assertAndApply(command).get();
    }
}
