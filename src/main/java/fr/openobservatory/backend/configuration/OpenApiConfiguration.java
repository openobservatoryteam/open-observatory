package fr.openobservatory.backend.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(new Info().title("OpenObservatory"));
  }
}
