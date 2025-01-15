package com.example.app.refdata.api.query;

import com.example.app.refdata.api.Operator;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import lombok.Value;

import java.util.List;

@Value
public class GetOperators implements Request<List<Operator>> {
    @HandleQuery
    List<Operator> handle() {
        return FluxCapacitor.search(Operator.class).fetchAll();
    }
}
