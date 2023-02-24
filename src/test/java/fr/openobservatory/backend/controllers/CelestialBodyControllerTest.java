package fr.openobservatory.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(CelestialBodyController.class)
public class CelestialBodyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldSearch() {
    }

    @Test
    void findById() {
    }

    @Test
    void shouldCreateCelestialBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/celestial-bodies")
                .content(asJsonString(new CreateCelestialBodyDto("name", 4)))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}