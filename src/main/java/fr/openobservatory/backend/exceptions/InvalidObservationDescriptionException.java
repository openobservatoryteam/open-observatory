package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "INVALID_OBSERVATION_DESCRIPTION")
public class InvalidObservationDescriptionException extends RuntimeException {}