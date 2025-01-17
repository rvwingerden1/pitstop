package com.example.app.user.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.app.user.api.Email;
import com.example.app.user.api.UserDetails;
import com.example.app.user.api.UserId;
import com.example.app.user.api.command.CreateUser;
import com.example.app.user.api.query.IsDefaultAdmin;
import io.fluxcapacitor.common.api.HasMetadata;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.UnauthenticatedException;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.UnauthorizedException;
import io.fluxcapacitor.javaclient.web.WebRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static io.fluxcapacitor.javaclient.FluxCapacitor.queryAndWait;
import static io.fluxcapacitor.javaclient.common.Message.asMessage;
import static io.fluxcapacitor.javaclient.configuration.ApplicationProperties.getProperty;

@Slf4j
public class AuthenticationUtils {
    public static final String authorizationHeader = "Authorization";

    private static final JWTVerifier jwtVerifier = new KeyBasedJwtVerifier();

    public static Sender getSender(HasMetadata request) {
        Optional<DecodedJWT> decodedJWT = decode(request);
        return decodedJWT
                .filter(jwt -> Objects.equals(getProperty("jwk.domain"), jwt.getIssuer()))
                .map(jwt -> {
                    try {
                        return jwtVerifier.verify(jwt);
                    } catch (TokenExpiredException e) {
                        throw new UnauthenticatedException("Token has expired");
                    } catch (Exception e) {
                        log.warn("Failed to verify sender from token: {}", jwt, e);
                        throw new UnauthorizedException("Token could not be verified");
                    }
                })
                .or(() -> decodedJWT).map(AuthenticationUtils::toSender).orElse(null);
    }

    private static Optional<DecodedJWT> decode(HasMetadata request) {
        return WebRequest.getHeader(request.getMetadata(), authorizationHeader)
                .map(header -> header.replace("Bearer ", ""))
                .map(token -> {
                    try {
                        return JWT.decode(token);
                    } catch (Exception e) {
                        return null;
                    }
                });
    }

    static Sender toSender(DecodedJWT decodedJWT) {
        var userId = new UserId(decodedJWT.getSubject());
        var userProfile = FluxCapacitor.loadAggregate(userId);
        if (userProfile.isEmpty()) {
            Email email = new Email(decodedJWT.getClaim("email").asString());
            boolean admin = queryAndWait(asMessage(new IsDefaultAdmin(email)).addUser(Sender.system));
            userProfile.assertAndApply(new CreateUser(userId, UserDetails.builder()
                    .firstName(decodedJWT.getClaim("given_name").asString())
                    .lastName(decodedJWT.getClaim("family_name").asString())
                    .email(email).build(), admin ? Role.admin : Role.user));
        }
        return Sender.createSender(userProfile.get());
    }

    public static String createAuthorizationHeader(UserId userId) {
        return "Bearer "  + createJwtToken(userId);
    }

    static String createJwtToken(UserId userId) {
        var now = FluxCapacitor.currentClock().instant();
        var userProfile = FluxCapacitor.loadAggregate(userId).get();
        if (userProfile == null) {
            return null;
        }
        JWTCreator.Builder builder = JWT.create()
                .withKeyId("fluxjwt")
                .withSubject(userId.getFunctionalId())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(Duration.ofHours(12))))
                .withClaim("given_name", userProfile.getDetails().getFirstName())
                .withClaim("family_name", userProfile.getDetails().getLastName())
                .withClaim("email", userProfile.getDetails().getEmail().toString());
        return builder.sign(Algorithm.none());
    }
}
