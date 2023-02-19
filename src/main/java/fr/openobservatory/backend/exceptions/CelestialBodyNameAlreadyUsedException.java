package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    code = HttpStatus.CONFLICT,
    reason = "The name is already used by another celestial body.")
public class CelestialBodyNameAlreadyUsedException extends RuntimeException {}
