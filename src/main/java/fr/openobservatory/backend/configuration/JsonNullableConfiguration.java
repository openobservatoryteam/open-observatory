package fr.openobservatory.backend.configuration;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonNullableConfiguration {

  @Bean
  public JsonNullableModule jsonNullableModule() {
    return new JsonNullableModule();
  }
}
