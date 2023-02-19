package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The validity time of the celestial body is not in the expected range.")
public class InvalidCelestialBodyValidityTimeException extends RuntimeException {}
