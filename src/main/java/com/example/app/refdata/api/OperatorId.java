package com.example.app.refdata.api;

import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.Id;

public class OperatorId extends Id<Operator> {
    public static OperatorId createNew() {
        return new OperatorId(FluxCapacitor.generateId());
    }

    public OperatorId(String id) { super(id);}
}
