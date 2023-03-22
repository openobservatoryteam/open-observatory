package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "UNAVAILABLE_ISS_POSITIONS")
public class UnavailableISSPositionsException extends RuntimeException {}
