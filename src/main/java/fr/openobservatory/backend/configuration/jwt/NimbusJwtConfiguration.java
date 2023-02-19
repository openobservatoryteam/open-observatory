package fr.openobservatory.backend.configuration.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import fr.openobservatory.backend.configuration.RsaKeyProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@AllArgsConstructor
@Configuration
public class NimbusJwtConfiguration {

  private final RsaKeyProperties keyProperties;

  // ---

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(keyProperties.publicKey()).build();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    var jwk =
        new RSAKey.Builder(keyProperties.publicKey())
            .privateKey(keyProperties.privateKey())
            .build();
    var jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSet);
  }
}
