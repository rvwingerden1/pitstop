package com.example.app.user.api.query;

import com.example.app.user.api.UserProfile;
import com.example.app.user.authentication.RequiresRole;
import com.example.app.user.authentication.Role;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import lombok.Value;

import java.util.List;

@Value
@RequiresRole(Role.admin)
public class GetUsers implements Request<List<UserProfile>> {
    @HandleQuery
    List<UserProfile> handle() {
        return FluxCapacitor.search(UserProfile.class).fetchAll();
    }
}
