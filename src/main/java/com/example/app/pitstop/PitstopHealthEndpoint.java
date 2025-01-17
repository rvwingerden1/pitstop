package com.example.app.pitstop;

import io.fluxcapacitor.javaclient.web.HandleGet;
import org.springframework.stereotype.Component;

@Component
public class PitstopHealthEndpoint {
    @HandleGet("/api/health")
    String checkHealth() {
        return "pitstop healthy";
    }
}
