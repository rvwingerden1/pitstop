package com.example.app.pitstop;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.OfferId;
import com.example.app.pitstop.handler.IncidentCommandHandler;
import com.example.app.pitstop.handler.IncidentQueryHandler;
import io.fluxcapacitor.common.FileUtils;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import io.fluxcapacitor.javaclient.web.WebResponse;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.time.ZoneOffset.UTC;

class PitStopViaApiTest {
    final Instant NOW = LocalDateTime.parse("2020-12-01T09:41:52.300").atZone(UTC).toInstant();
    final TestFixture testFixture = TestFixture
                    .create(PitStopApi.class, IncidentCommandHandler.class, IncidentQueryHandler.class)
                    .withClock(Clock.fixed(NOW, ZoneId.systemDefault()));

    @Test
    void getViaApi() {
        testFixture.whenGet("/api/incidents").<List<?>>expectResult(List::isEmpty);
    }

    @Test
    void postIncidentDetails() {
        testFixture
                .whenPost("/api/incidents", FileUtils.loadFile("/pitstop/incident-details.json"))
                .expectWebResponses(new IncidentId("0"));
    }

    @Test
    void getIncidentDetailsAfterPost() {
        testFixture
                .givenPost("/api/incidents", FileUtils.loadFile("/pitstop/incident-details.json"))
                .whenGet("/api/incidents")
                .<List<Incident>>mapResult(webResponse -> ((WebResponse)webResponse).getPayload())
                .mapResult(List::getFirst)
                .expectResult("/pitstop/expected/incident.json");
    }

    @Test
    void offerAssistance() {
        testFixture
                .givenPost("/api/incidents", FileUtils.loadFile("/pitstop/incident-details.json"))
                .whenPost("/api/incidents/0/offers", FileUtils.loadFile("/pitstop/offer-details.json"))
                .expectWebResponses(new OfferId("1"))
                .andThen()
                .whenGet("api/incidents")
                .<List<Incident>>mapResult(webResponse -> ((WebResponse)webResponse).getPayload())
                .mapResult(List::getFirst)
                .expectResult("/pitstop/expected/incident-with-offer.json");
    }

    @Test
    void testCorsPreflight() {
        testFixture.whenWebRequest(WebRequest.builder().method(HttpRequestMethod.OPTIONS)
                        .url("/api/user").build())
                .expectSuccessfulResult();
    }
}