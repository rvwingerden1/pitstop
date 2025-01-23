package com.example.app.pitstop;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.pitstop.api.command.ReportIncident;
import com.example.app.pitstop.api.query.FindIncidents;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.web.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Path("/api")
public class PitStopApi {

    @HandlePost("incidents")
    IncidentId reportIncident(IncidentDetails details) {
        IncidentId incidentId = IncidentId.newValue();
        FluxCapacitor.sendCommandAndWait(ReportIncident.builder()
                .incidentId(incidentId)
                .details(details)
                .build());
        return incidentId;
    }

    @HandleGet("incidents")
    List<Incident> getIncidents() {
        return FluxCapacitor.queryAndWait(new FindIncidents());
    }

    @HandlePost("incidents/{incidentId}/offers")
    OfferId offerAssistance(@PathParam IncidentId incidentId, OfferDetails details) {
        OfferId offerId = OfferId.newValue();
        FluxCapacitor.sendCommandAndWait(OfferAssistance.builder()
                .incidentId(incidentId)
                .offer(Offer.builder().offerId(offerId).details(details).build())
                .build());
        return offerId;
    }

    @HandlePost("incidents/{incidentId}/offers/{offerId}/accept")
    void acceptOffer(@PathParam IncidentId incidentId, @PathParam OfferId offerId) {
        throw new UnsupportedOperationException();
    }

    @HandlePost("incidents/{incidentId}/close")
    void closeIncident(@PathParam IncidentId incidentId) {
        throw new UnsupportedOperationException();
    }

    @Path("/api/*")
    @HandleOptions
    WebResponse corsPreflight() {
        return WebResponse.builder()
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, HEAD, TRACE")
                .header("Access-Control-Max-Age", String.valueOf(Duration.ofDays(1).toSeconds()))
                .build();
    }

}
