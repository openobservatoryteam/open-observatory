package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "OBSERVATION_NOT_EDITABLE")
public class ObservationNotEditableException extends RuntimeException {}
