package com.example.app.pitstop;

import com.example.app.external_api.AaaOrder;
import com.example.app.external_api.AcceptAaaOffer;
import com.example.app.external_api.AnotherAaaHandler;
import com.example.app.pitstop.api.command.EscalateIncident;
import com.example.app.pitstop.handler.IncidentCommandHandler;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.PathParam;
import org.junit.jupiter.api.Test;

class AnotherAaaWorkOrderTest {

    final TestFixture testFixture = TestFixture.create(
                    PitStopApi.class, IncidentCommandHandler.class, IncidentLifeCycleHandler.class,
                    AnotherAaaHandler.class,
                    AaaMock.class, AaaOrder.class)
            .withProperty("aaa.token", "789798")
            .withProperty("aaa.domain", "");

    @Test
    void reportViaApi() {
        testFixture
                .givenPost("/api/incidents", "/pitstop/incident-details.json")
                .whenTimeElapses(IncidentLifeCycleHandler.ESCALATE)
                .expectEvents(EscalateIncident.class)
                .expectWebRequest(r -> true)
                .andThen()
                .whenCommand(AcceptAaaOffer.builder().offerId("123").build())
                .expectEvents("/pitstop/expected/offer-assistance.json")
                .andThen()
                .whenApplying(fluxCapacitor -> {
                    Object o = fluxCapacitor.keyValueStore().get("123");
                    return true;
                });
//                .whenTimeElapses(Duration.ofMinutes(1))
//                .expectEvents(UpdateAssistance.class)
//                .andThen()
//                .whenCommand("/pitstop/close-incident.json")
//                .expectNoSchedules();
    }

    static class AaaMock {

        @HandlePost("/aaa/orders")
        String handlePost() {
            return "123";
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