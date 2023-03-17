package fr.openobservatory.backend.configuration;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

  @Bean
  public ModelMapper modelMapper() {
    var modelMapper = new ModelMapper();
    modelMapper.addConverter(
        new Converter<Instant, OffsetDateTime>() {
          @Override
          public OffsetDateTime convert(MappingContext<Instant, OffsetDateTime> context) {
            return context.getSource().atOffset(ZoneOffset.UTC);
          }
        });
    return modelMapper;
  }
}
