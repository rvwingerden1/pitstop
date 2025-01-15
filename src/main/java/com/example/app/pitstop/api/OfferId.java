package com.example.app.pitstop.api;

import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.Id;

public class OfferId extends Id<Offer> {
    public static OfferId newValue() {
        return new OfferId(FluxCapacitor.generateId());
    }

    public OfferId(String id) { super(id);}
}
