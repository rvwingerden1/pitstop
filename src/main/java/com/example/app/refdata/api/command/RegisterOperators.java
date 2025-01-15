package com.example.app.refdata.api.command;

import com.example.app.user.authentication.RequiresRole;
import com.example.app.user.authentication.Role;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.Value;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Value
@RequiresRole(Role.admin)
public class RegisterOperators {
    List<@Valid RegisterOperator> commands;

    @HandleCommand
    @SneakyThrows
    void handle() {
        CompletableFuture.allOf(FluxCapacitor.sendCommands(commands.toArray()).toArray(CompletableFuture[]::new)).get();
    }
}
