package com.example.app.pitstop;

import com.example.app.external_api.AaaHandler;
import com.example.app.external_api.AcceptAaaOffer;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.pitstop.api.command.ReportIncident;
import com.example.app.pitstop.handler.IncidentCommandHandler;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.PathParam;
import org.junit.jupiter.api.Test;

import java.util.List;

class AaaOrderTest {
    public static final IncidentId INCIDENT_ID = new IncidentId("test_incident");
    public static final IncidentDetails INCIDENT_DETAILS = JsonUtils.fromFile(
            "/pitstop/incident-details.json", IncidentDetails.class);
    public static final ReportIncident REPORT_INCIDENT = ReportIncident.builder()
            .incidentId(INCIDENT_ID)
            .details(INCIDENT_DETAILS)
            .build();

    final TestFixture testFixture = TestFixture.create(
            PitStopApi.class, IncidentCommandHandler.class, AaaHandler.class,
            AaaMock.class)
            .withProperty("aaa.token", "789798")
            .withProperty("aaa.domain", "");

    @Test
    void escalateInvokesAaaApi() {
        testFixture
                .givenCommands(REPORT_INCIDENT)
                .givenEvents(EscalateIncident.builder().incidentId(INCIDENT_ID).build())
                .whenCommand(AcceptAaaOffer.builder().offerId("test").build())
                .expectThat(fluxCapacitor -> {
                    List<Object> objects = fluxCapacitor.documentStore().search("aaa").fetchAll();
                    System.out.println(objects);
                })
                .expectCommands(OfferAssistance.class);
    }


    static class AaaMock {

        @HandlePost("/aaa/orders")
        String handlePost() {
            return "test";
        }

        @HandleGet("/aaa/orders/{orderId}")
        String handlePost(@PathParam String orderId) {
            return """
                    {
                      "plannedArrival" : "2025-01-17T16:00:00.000+01:00",
                      "workStatus" : "arrived"
                    }
                    """;
        }


    }


}