package com.example.app.refdata.api.query;

import com.example.app.user.authentication.RequiresRole;
import com.example.app.user.authentication.Role;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.UnauthorizedException;
import io.fluxcapacitor.javaclient.tracking.handling.validation.ValidationException;
import io.fluxcapacitor.javaclient.web.WebRequest;
import io.fluxcapacitor.javaclient.web.WebRequestSettings;
import io.fluxcapacitor.javaclient.web.WebResponse;

import java.util.Set;

import static java.lang.String.format;

@RequiresRole(Role.admin)
public abstract class SendWebRequest {
    protected abstract WebRequest.Builder buildRequest(WebRequest.Builder requestBuilder, Sender sender);

    @HandleQuery
    @HandleCommand
    public Object handle(Sender sender) {
        WebRequest request = buildRequest(WebRequest.builder(), sender).build();
        WebResponse webResponse = FluxCapacitor.get().webRequestGateway().sendAndWait(request, requestSettings());
        return handleResponse(webResponse, request);
    }

    protected WebRequestSettings requestSettings() {
        return WebRequestSettings.builder().build();
    }

    protected Object handleResponse(WebResponse response, WebRequest request) {
        String body = bodyAsString(response);
        switch (response.getStatus()) {
            case 400 -> throw new ValidationException(
                    format("Invalid request (status %s) to endpoint %s. Response: %s.",
                           response.getStatus(), request.getPath(), body), Set.of());
            case 401 -> throw new UnauthorizedException(
                    format("Unauthorized (status %s) for endpoint %s. Response: %s.",
                           response.getStatus(), request.getPath(), body));
            case 403, 429 -> throw new IllegalCommandException(
                    format("Illegal request (status %s) to endpoint %s. Response: %s.",
                           response.getStatus(), request.getPath(), body));
            case 404 -> {
                return null;
            }
        }
        if (response.getStatus() >= 300) {
            throw new IllegalStateException(
                    format("Error (status %s) when invoking endpoint %s. Response: %s.",
                           response.getStatus(), request.getPath(), body));
        }
        return body;
    }

    protected String bodyAsString(WebResponse response) {
        if (response.getPayload() instanceof byte[] bytes) {
            return new String(bytes);
        }
        if (response.getPayload() instanceof String s) {
            return s;
        }
        throw new IllegalStateException("Unexpected response: " + response.getPayload());
    }
}
