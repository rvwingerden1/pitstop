package com.example.app.pitstop;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.api.command.AcceptOffer;
import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.pitstop.api.command.ReportIncident;
import com.example.app.pitstop.api.query.FindIncidents;
import com.example.app.pitstop.handler.IncidentCommandHandler;
import com.example.app.pitstop.handler.IncidentQueryHandler;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.Entity;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.time.ZoneOffset.UTC;

class PitStopTest {
    final Instant NOW = LocalDateTime.parse("2020-12-01T09:41:52.300").atZone(UTC).toInstant();
    final TestFixture testFixture = TestFixture.create(IncidentCommandHandler.class,
                    IncidentQueryHandler.class)
            .withClock(Clock.fixed(NOW, ZoneId.systemDefault()));

    public static final IncidentId INCIDENT_ID = new IncidentId("test_incident");
    public static final OfferId OFFER_ID = new OfferId("test_offer");
    public static final AcceptOffer ACCEPT_OFFER = AcceptOffer.builder()
            .incidentId(INCIDENT_ID).offerId(OFFER_ID).build();
    public static final CloseIncident CLOSE_INCIDENT = CloseIncident.builder()
            .incidentId(INCIDENT_ID).build();

    public static final IncidentDetails INCIDENT_DETAILS = JsonUtils.fromFile(
            "/pitstop/incident-details.json", IncidentDetails.class);
    public static final ReportIncident REPORT_INCIDENT = ReportIncident.builder()
            .incidentId(INCIDENT_ID)
            .details(INCIDENT_DETAILS)
            .build();

    public static final OfferDetails OFFER_DETAILS = JsonUtils.fromFile("/pitstop/offer-details.json", OfferDetails.class);
    public static final Offer OFFER = Offer.builder().offerId(OFFER_ID).details(OFFER_DETAILS).build();
    public static final OfferAssistance OFFER_ASSISTANCE = OfferAssistance.builder()
            .incidentId(INCIDENT_ID)
            .offer(OFFER)
            .build();

    @Test
    void reportIncident() {
        testFixture.whenCommand(REPORT_INCIDENT)
                .expectEvents(ReportIncident.class);
    }

    @Test
    void getIncidentDetails() {
        testFixture.givenCommands(REPORT_INCIDENT)
                .whenQuery(new FindIncidents())
                .expectResult(List.of(Incident.builder()
                        .incidentId(INCIDENT_ID)
                        .details(INCIDENT_DETAILS)
                        .start(NOW)
                        .build()));
    }

    @Test
    void offerAssistance() {
        testFixture.givenCommands(REPORT_INCIDENT)
                .whenCommand(OFFER_ASSISTANCE)
                .expectEvents(OfferAssistance.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    void acceptOffer() {
        // FindIncidents implements Request<List<Incident>>, therefore no casting is necessary!
        testFixture.givenCommands(REPORT_INCIDENT, OFFER_ASSISTANCE)
                .whenCommand(ACCEPT_OFFER)
                .andThen()
                .whenQuery(new FindIncidents())
                .mapResult(incidents -> incidents.getFirst().getOffers().getFirst())
                .expectResult(Offer::isAccepted);
    }

    @Test
    void acceptOfferMostCleanTest() {
        testFixture.givenCommands(REPORT_INCIDENT, OFFER_ASSISTANCE)
                .whenCommand(ACCEPT_OFFER)
                .expectThat(fluxCapacitor -> {
                    FluxCapacitor.loadEntity(OFFER_ID)
                            .mapIfPresent(Entity::get)
                            .map(Offer::isAccepted)
                            .orElseThrow(() -> new AssertionError("Offer is not accepted"));
                    FluxCapacitor.loadAggregateFor(OFFER_ID, Offer.class)
                            .mapIfPresent(Entity::get)
                            .map(o -> (Incident) o)
                            .map(incident -> incident.getOffers().getFirst())
                            .map(Offer::isAccepted)
                            .orElseThrow(() -> new AssertionError("Accepted Offer is niet bijgewerkt in het incident"));
                });
    }

    @Test
    void closeIncident() {
        testFixture.givenCommands(REPORT_INCIDENT, OFFER_ASSISTANCE, ACCEPT_OFFER)
                .whenCommand(CLOSE_INCIDENT)
                .expectEvents(CLOSE_INCIDENT);
    }

    @Test
    void closingIncidentOnlyWhenOfferAccepted() {
        testFixture.givenCommands(REPORT_INCIDENT, OFFER_ASSISTANCE)
                .whenCommand(CLOSE_INCIDENT)
                .expectError(IllegalCommandException.class);
    }

    @Test
    void alreadyClosedIncidentCanNotBeClosedAgain() {
        testFixture.givenCommands(REPORT_INCIDENT, OFFER_ASSISTANCE, ACCEPT_OFFER, CLOSE_INCIDENT)
                .whenCommand(CLOSE_INCIDENT)
                .expectError(IllegalCommandException.class);
    }

    @Test
    void cannotCloseIncidentWithoutReportingFirst() {
        testFixture
                .whenCommand(CLOSE_INCIDENT)
                .expectError(IllegalCommandException.class);
    }
}