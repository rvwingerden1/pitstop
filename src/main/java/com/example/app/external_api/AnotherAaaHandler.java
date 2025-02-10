package com.example.app.external_api;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.refdata.api.OperatorId;
import com.example.app.user.api.UserProfile;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

import static io.fluxcapacitor.javaclient.FluxCapacitor.queryAndWait;
import static io.fluxcapacitor.javaclient.FluxCapacitor.sendCommandAndWait;

@Value
@Builder(toBuilder = true)
public class AnotherAaaHandler {

    @HandleEvent
    void handle(EscalateIncident escalateIncident) {
        String aaaId = processEscalation(escalateIncident);
        PitstopToAaaInstance instance =
                PitstopToAaaInstance.builder().aaaId(aaaId).incidentId(escalateIncident.getIncidentId()).build();
        FluxCapacitor.get().keyValueStore().store(aaaId, instance);
    }

    @HandleCommand
    void handle(AcceptAaaOffer acceptAaaOffer) {
        PitstopToAaaInstance instance = FluxCapacitor.get().keyValueStore().get(acceptAaaOffer.getOfferId());
        OfferAssistance offerAssistence = toOfferAssistence(instance.getIncidentId());
        sendCommandAndWait(offerAssistence);
        FluxCapacitor.get().keyValueStore().store(instance.getAaaId(), instance.toBuilder().offerIssued(true).build());
    }

    private OfferAssistance toOfferAssistence(IncidentId id) {
        return OfferAssistance.builder()
                .incidentId(id)
                .offer(
                        Offer.builder()
                                .offerId(OfferId.newValue())
                                .details(OfferDetails.builder()
                                        .price(BigDecimal.TEN)
                                        .operatorId(OperatorId.createNew())
                                        .build())
                                .build())
                .build();
    }

    private static String processEscalation(EscalateIncident escalateIncident) {
        Incident incident = FluxCapacitor.loadAggregate(escalateIncident.getIncidentId(), Incident.class).get();
        UserProfile user = FluxCapacitor.loadAggregate(incident.getReporter()).get();
        AaaOrder order = AaaOrder.from(incident, user);
        return queryAndWait(new PostAaaOrder("/aaa/orders", order));
    }

    @Value
    @Builder(toBuilder = true)
    static class PitstopToAaaInstance {
        IncidentId incidentId;
        String aaaId;

        boolean offerIssued;
    }
}
