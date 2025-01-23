package com.example.app.pitstop.api.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.Offer;
import com.example.app.pitstop.api.OfferId;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AcceptOffer {
    OfferId offerId;

    @Apply
    Offer apply(Offer offer) {
        return offer.toBuilder().accepted(true).build();
    }
}
