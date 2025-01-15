package com.example.app.web.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Value;

@Value
public class UiUpdate {
    Type type;
    @JsonSerialize(using= ToStringSerializer.class)
    Long index;
    String id;
    JsonNode patch;

    public enum Type {
        Operator, UserProfile, Incident
    }
}
