package fr.openobservatory.backend.configuration;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

@Configuration
public class PushServiceConfiguration {

    // No worries, these keys will eventually be replaced
    private static final String PRIVATE_KEY = "6K7GVsJREuzN8BqVbMhWZvDLT-8HiwNbPo3kWdzZaQA";
    private static final String PUBLIC_KEY = "BBa0zJTVfJBuHa0ud9BVgaH4bO1o2Dpe5bddHCskRG7LYRaOZdqBL7zlu_4qJashpNhrr9PrhAfYB1O1AiEW6vs";
    private static final String SUBJECT = "https://openobs-dev.kevinbioj.fr";

    // ---

    @Bean
    public PushService pushService() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());
        var pushService = new PushService();
        pushService.setPrivateKey(PRIVATE_KEY);
        pushService.setPublicKey(PUBLIC_KEY);
        pushService.setSubject(SUBJECT);
        return pushService;
    }
}
