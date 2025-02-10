package com.example.app.external_api;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.refdata.api.OperatorId;
import com.example.app.user.api.UserProfile;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.EntityId;
import io.fluxcapacitor.javaclient.tracking.Consumer;
import io.fluxcapacitor.javaclient.tracking.handling.Association;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import io.fluxcapacitor.javaclient.tracking.handling.Stateful;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

import static io.fluxcapacitor.javaclient.FluxCapacitor.queryAndWait;
import static io.fluxcapacitor.javaclient.FluxCapacitor.sendCommandAndWait;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Stateful(collection = "aaa")
@Value
@Builder(toBuilder = true)
@Consumer(name = "aaaProcess")
public class AaaHandler {
    @EntityId
    IncidentId incidentId;

    @Association
    String aaaId;

    boolean offerIssued;

    @HandleEvent
    static AaaHandler create(EscalateIncident escalateIncident) {
        return AaaHandler
                .builder()
                .aaaId(processEscalation(escalateIncident))
                .incidentId(escalateIncident.getIncidentId())
                .build();
    }

    @HandleCommand
    @Association("offerId")
    AaaHandler handle(AcceptAaaOffer acceptAaaOffer) {
        OfferAssistance offerAssistence = toOfferAssistence();
        sendCommandAndWait(offerAssistence);
        return toBuilder().offerIssued(true).build();
    }

    private OfferAssistance toOfferAssistence() {
        return OfferAssistance.builder()
                .incidentId(this.incidentId)
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
}
