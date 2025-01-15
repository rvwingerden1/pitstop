package com.example.app.refdata.api.query;

import com.example.app.pitstop.api.Vehicle;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import lombok.Value;

import java.util.List;

@Value
public class FindVehicles implements Request<List<Vehicle>> {
    String term;
}
