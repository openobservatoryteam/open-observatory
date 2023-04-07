package fr.openobservatory.backend.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openobservatory.backend.configuration.PushServiceProperties;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Component
public class PushProvider {

    private final ObjectMapper objectMapper;
    private final PushServiceProperties properties;
    private final PushService pushService;

    // ---

    /**
     * Returns the public key used to sign push notifications.
     */
    public String publicKey() {
        return properties.publicKey();
    }

    /**
     * Sends the requested push message over the network.
     * @param message The push message to send.
     */
    public void send(PushMessage message) throws PushMessageException {
        try {
            var payload = objectMapper.writeValueAsBytes(message.payload);
            var notification = Notification.builder()
                    .endpoint(message.endpoint)
                    .userAuth(message.auth)
                    .userPublicKey(message.publicKey)
                    .payload(payload)
                    .build();
            var response = pushService.send(notification);
            if (!HttpStatus.valueOf(response.getStatusLine().getStatusCode()).is2xxSuccessful())
                throw new PushMessageException();
        } catch (InterruptedException e) {
            send(message);
        } catch (JoseException | GeneralSecurityException | IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // ---

    // ---

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public static class PushMessageException extends Exception {
    }

    public record PushMessage(String endpoint, String auth, String publicKey, Object payload) {}
}
