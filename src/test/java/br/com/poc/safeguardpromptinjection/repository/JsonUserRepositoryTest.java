package br.com.poc.safeguardpromptinjection.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonUserRepositoryTest {

    @Test
    void shouldLoadTheSameTwoUsersUsedByTheOriginalProject() throws Exception {
        JsonUserRepository repository = new JsonUserRepository(new ObjectMapper());

        assertThat(repository.findByUsername("erickwendel")).isPresent();
        assertThat(repository.findByUsername("ananeri")).isPresent();
        assertThat(repository.findByUsername("ananeri").orElseThrow().role()).isEqualTo("member");
    }
}
