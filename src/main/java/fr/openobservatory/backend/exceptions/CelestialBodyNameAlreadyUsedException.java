package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "CELESTIAL_BODY_NAME_ALREADY_USED")
public class CelestialBodyNameAlreadyUsedException extends RuntimeException {}
