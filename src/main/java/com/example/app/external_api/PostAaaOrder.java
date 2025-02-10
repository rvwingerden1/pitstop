package com.example.app.external_api;

import com.example.app.refdata.api.query.SendWebRequest;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.configuration.ApplicationProperties;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import lombok.Value;

@Value
public class PostAaaOrder extends SendWebRequest implements Request<String> {
    String resource;
    AaaOrder aaaOrder;

    @Override
    protected WebRequest.Builder buildRequest(WebRequest.Builder requestBuilder, Sender sender) {
        return requestBuilder
                .url(ApplicationProperties.requireProperty("aaa.domain") + resource)
                .method(HttpRequestMethod.POST)
                .payload(aaaOrder)
                .header("X-App-Token", ApplicationProperties.requireProperty("aaa.token"));
    }
}
