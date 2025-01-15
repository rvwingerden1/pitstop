package com.example.app.refdata.api.command;

import com.example.app.refdata.api.Operator;
import com.example.app.refdata.api.OperatorDetails;
import com.example.app.refdata.api.OperatorId;
import com.example.app.user.api.UserId;
import com.example.app.user.api.command.AuthorizeForOperator;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import static io.fluxcapacitor.javaclient.common.Message.asMessage;

@Value
@RequiresUser
public class RegisterOperator implements Request<OperatorId> {
    @NotNull
    OperatorId operatorId = OperatorId.createNew();
    @NotNull @Valid
    OperatorDetails details;
    UserId owner;

    @HandleCommand
    OperatorId handle() {
        FluxCapacitor.loadAggregate(getOperatorId()).assertAndApply(this);
        return getOperatorId();
    }

    @AssertLegal
    void assertAuthorizedForOwner(Sender sender) {
        if (owner != null && !sender.isAuthorizedFor(owner)) {
            throw new IllegalCommandException("Sender is not authorized for given owner");
        }
    }

    @Apply
    Operator create() {
        if (owner != null) {
            FluxCapacitor.loadAggregate(owner).assertAndApply(
                    asMessage(new AuthorizeForOperator(owner, operatorId)).addUser(Sender.system));
        }
        return Operator.builder().operatorId(operatorId).details(details).build();
    }
}
