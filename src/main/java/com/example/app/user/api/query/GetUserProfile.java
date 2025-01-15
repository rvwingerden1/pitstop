package com.example.app.user.api.query;

import com.example.app.user.api.UserProfile;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import jakarta.annotation.Nullable;
import lombok.Value;

@Value
public class GetUserProfile implements Request<UserProfile> {

    @HandleQuery
    UserProfile handle(@Nullable Sender sender) {
        return sender == null ? null : FluxCapacitor.loadAggregate(sender.getUserId()).get();
    }
}
