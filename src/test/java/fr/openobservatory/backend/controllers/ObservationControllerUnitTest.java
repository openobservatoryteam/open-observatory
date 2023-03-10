package fr.openobservatory.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import fr.openobservatory.backend.dto.ObservationDetailedDto;
import fr.openobservatory.backend.services.ObservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ObservationControllerUnitTest {

  @Mock private ObservationService observationService;

  @InjectMocks private ObservationController observationController;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetObservation() {
    // GIVEN
    Long id = 1L;
    ObservationDetailedDto observation = new ObservationDetailedDto();
    observation.setId(id);
    when(observationService.findById(id)).thenReturn(observation);

    // WHEN
    ResponseEntity<ObservationDetailedDto> response = observationController.getObservation(id);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(id, response.getBody().getId());
  }

  @Test
  void testGetObservationNotFound() {
    // GIVEN
    Long id = 1L;
    when(observationService.findById(id)).thenReturn(null);

    // WHEN
    ResponseEntity<ObservationDetailedDto> response = observationController.getObservation(id);

    // THEN
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
