package fr.openobservatory.backend.Implementations.Controllers;

import fr.openobservatory.backend.Implementations.Dtos.ObservationDto;
import fr.openobservatory.backend.Interfaces.IServices.IObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping(path = "/") @RequiredArgsConstructor
public class ObservationController
{
    private final IObservationService observationService;

    @GetMapping(path = "/observations")
    public ResponseEntity<List<ObservationDto>> observations(int limit, int page)
    {
        return ResponseEntity.ok(observationService.searchObservations(limit,page));
    }
}
