package fr.openobservatory.backend.configuration;

import java.security.GeneralSecurityException;
import java.security.Security;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class PushServiceConfiguration {

  private final PushServiceProperties configuration;

  // ---

  @Bean
  public PushService pushService() throws GeneralSecurityException {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
      Security.addProvider(new BouncyCastleProvider());
    return new PushService(
        configuration.publicKey(), configuration.privateKey(), configuration.subject());
  }
}
