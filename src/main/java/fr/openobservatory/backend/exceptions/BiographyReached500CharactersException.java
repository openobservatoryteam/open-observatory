package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "BIOGRAPHY_REACHED_500_CHARACTERS")
public class BiographyReached500CharactersException extends RuntimeException {}
