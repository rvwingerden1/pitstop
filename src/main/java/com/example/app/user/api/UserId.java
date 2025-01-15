package com.example.app.user.api;

import io.fluxcapacitor.javaclient.modeling.Id;

public class UserId extends Id<UserProfile> {
    public UserId(String functionalId) {
        super(functionalId);
    }
}
