package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    code = HttpStatus.BAD_REQUEST,
    reason = "The name of the celestial body does not match the expected format.")
public class InvalidCelestialBodyNameException extends RuntimeException {}
