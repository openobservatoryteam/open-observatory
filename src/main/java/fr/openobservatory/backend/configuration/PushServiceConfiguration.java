package fr.openobservatory.backend.configuration;

import java.security.GeneralSecurityException;
import java.security.Security;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushServiceConfiguration {

  public static final String PUBLIC_KEY =
      "BBa0zJTVfJBuHa0ud9BVgaH4bO1o2Dpe5bddHCskRG7LYRaOZdqBL7zlu_4qJashpNhrr9PrhAfYB1O1AiEW6vs";
  private static final String PRIVATE_KEY = "6K7GVsJREuzN8BqVbMhWZvDLT-8HiwNbPo3kWdzZaQA";
  private static final String SUBJECT = "https://openobs-dev.kevinbioj.fr";

  // ---

  @Bean
  public PushService pushService() throws GeneralSecurityException {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
      Security.addProvider(new BouncyCastleProvider());
    return new PushService(PUBLIC_KEY, PRIVATE_KEY, SUBJECT);
  }
}
