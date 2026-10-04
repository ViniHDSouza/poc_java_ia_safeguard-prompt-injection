package br.com.poc.safeguardpromptinjection.repository;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mantém o mesmo conceito do projeto original: uma "base" de usuários em JSON.
 * Não existe banco de dados porque isso desviaria o foco da aula de Prompt Injection.
 */
@Repository
public class JsonUserRepository implements UserRepository {

    private final Map<String, ApplicationUser> usersByUsername;

    public JsonUserRepository(ObjectMapper objectMapper) throws IOException {
        ClassPathResource usersResource = new ClassPathResource("data/users.json");
        this.usersByUsername = objectMapper.readValue(
                usersResource.getInputStream(),
                new TypeReference<LinkedHashMap<String, ApplicationUser>>() { }
        );
    }

    @Override
    public Optional<ApplicationUser> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public Collection<ApplicationUser> findAll() {
        return usersByUsername.values();
    }
}
