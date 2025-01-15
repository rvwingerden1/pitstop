package com.example.app.pitstop.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Offer {
    OfferId offerId;
    OfferDetails details;

    boolean accepted;
}
