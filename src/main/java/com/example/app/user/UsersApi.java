package com.example.app.user;

import com.example.app.user.api.UserDetails;
import com.example.app.user.api.UserId;
import com.example.app.user.api.UserProfile;
import com.example.app.user.api.command.CreateUser;
import com.example.app.user.api.query.GetUserProfile;
import com.example.app.user.api.query.GetUsers;
import com.example.app.user.authentication.Role;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.Path;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Path("/api")
public class UsersApi {
    @HandleGet("/user")
    UserProfile getUser() {
        return FluxCapacitor.queryAndWait(new GetUserProfile());
    }

    @HandlePost("/users")
    UserId createUser(UserDetails details) {
        var userId = new UserId(FluxCapacitor.generateId());
        FluxCapacitor.sendCommandAndWait(new CreateUser(userId, details, Role.user));
        return userId;
    }

    @HandleGet("/users")
    List<UserProfile> getUsers() {
        return FluxCapacitor.queryAndWait(new GetUsers());
    }

}
