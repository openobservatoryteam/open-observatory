package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    code = HttpStatus.NOT_FOUND,
    reason = "UNKNOWN_CELESTIAL_BODY")
public class UnknownCelestialBodyException extends RuntimeException {}
