package fr.openobservatory.backend;

import fr.openobservatory.backend.configuration.PushServiceProperties;
import fr.openobservatory.backend.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({PushServiceProperties.class, RsaKeyProperties.class})
@SpringBootApplication
public class OpenObservatoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenObservatoryApplication.class, args);
  }
}
