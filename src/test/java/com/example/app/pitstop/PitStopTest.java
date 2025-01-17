package com.example.app.pitstop;

import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

class PitStopTest {

    final TestFixture testFixture = TestFixture.create(PitStopApi.class);

    @Test
    void getViaApi() {
        testFixture.whenGet("/api/incidents").<List<?>>expectResult(List::isEmpty);
    }

    @Test
    void testCorsPreflight() {
        testFixture.whenWebRequest(WebRequest.builder().method(HttpRequestMethod.OPTIONS)
                                           .url("/api/user").build())
                .expectSuccessfulResult();
    }
}