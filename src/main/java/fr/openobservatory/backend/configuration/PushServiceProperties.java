package fr.openobservatory.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push")
public record PushServiceProperties(String privateKey, String publicKey, String subject) {}
