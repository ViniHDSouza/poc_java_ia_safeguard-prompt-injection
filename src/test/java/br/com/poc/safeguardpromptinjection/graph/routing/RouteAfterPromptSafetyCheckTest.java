package br.com.poc.safeguardpromptinjection.graph.routing;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import br.com.poc.safeguardpromptinjection.graph.SafeguardWorkflowState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RouteAfterPromptSafetyCheckTest {

    private final RouteAfterPromptSafetyCheck router = new RouteAfterPromptSafetyCheck();

    @Test
    void shouldGoDirectlyToChatWhenGuardrailsAreDisabled() {
        SafeguardWorkflowState state = state(false, new GuardrailEvaluation(false, "blocked", "UNSAFE"));
        assertThat(router.apply(state)).isEqualTo(RouteAfterPromptSafetyCheck.CHAT_ROUTE);
    }

    @Test
    void shouldGoToBlockedNodeWhenGuardrailClassifiesRequestAsUnsafe() {
        SafeguardWorkflowState state = state(true, new GuardrailEvaluation(false, "blocked", "UNSAFE injection"));
        assertThat(router.apply(state)).isEqualTo(RouteAfterPromptSafetyCheck.BLOCKED_ROUTE);
    }

    @Test
    void shouldGoToChatWhenGuardrailClassifiesRequestAsSafe() {
        SafeguardWorkflowState state = state(true, new GuardrailEvaluation(true, "safe", "SAFE normal request"));
        assertThat(router.apply(state)).isEqualTo(RouteAfterPromptSafetyCheck.CHAT_ROUTE);
    }

    private SafeguardWorkflowState state(boolean enabled, GuardrailEvaluation evaluation) {
        return new SafeguardWorkflowState(Map.of(
                SafeguardWorkflowState.USER, new ApplicationUser("test", "member", List.of(), "Test User"),
                SafeguardWorkflowState.USER_PROMPT, "hello",
                SafeguardWorkflowState.GUARDRAILS_ENABLED, enabled,
                SafeguardWorkflowState.GUARDRAIL_EVALUATION, evaluation
        ));
    }
}
