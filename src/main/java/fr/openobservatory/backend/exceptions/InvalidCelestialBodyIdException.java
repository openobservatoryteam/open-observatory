package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "INVALID_CELESTIAL_BODY_ID")
public class InvalidCelestialBodyIdException extends RuntimeException {}
