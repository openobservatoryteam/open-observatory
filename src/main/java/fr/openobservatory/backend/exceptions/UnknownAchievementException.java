package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "ACHIEVEMENT_NOT_FOUND")
public class UnknownAchievementException extends RuntimeException {}
