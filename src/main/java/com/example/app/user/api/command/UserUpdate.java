package com.example.app.user.api.command;

import com.example.app.user.api.UserId;
import com.example.app.user.api.UserProfile;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.tracking.TrackSelf;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@TrackSelf
public interface UserUpdate {
    @NotNull
    UserId getUserId();

    @AssertLegal
    default void assertExistence(@Nullable UserProfile profile) {
        if (profile == null) {
            throw new IllegalCommandException("User not found");
        }
    }

    @HandleCommand
    default void handle() {
        FluxCapacitor.loadAggregate(getUserId()).assertAndApply(this);
    }
}
