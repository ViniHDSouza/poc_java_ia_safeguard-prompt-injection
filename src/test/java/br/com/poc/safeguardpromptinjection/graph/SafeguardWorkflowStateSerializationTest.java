package br.com.poc.safeguardpromptinjection.graph;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Protege um requisito importante do LangGraph4j: objetos carregados no estado devem poder ser serializados.
 */
class SafeguardWorkflowStateSerializationTest {

    @Test
    void shouldSerializeDomainObjectsStoredInsideGraphState() {
        ApplicationUser user = new ApplicationUser("ananeri", "member", List.of(), "Ana Neri");
        GuardrailEvaluation evaluation = new GuardrailEvaluation(false, "blocked", "UNSAFE");

        assertThatCode(() -> {
            try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                 ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(user);
                output.writeObject(evaluation);
            }
        }).doesNotThrowAnyException();
    }
}
