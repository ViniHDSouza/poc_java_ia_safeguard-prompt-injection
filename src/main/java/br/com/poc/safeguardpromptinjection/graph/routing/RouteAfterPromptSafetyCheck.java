package br.com.poc.safeguardpromptinjection.graph.routing;

import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import br.com.poc.safeguardpromptinjection.graph.SafeguardWorkflowState;
import org.bsc.langgraph4j.action.EdgeAction;
import org.springframework.stereotype.Component;

/**
 * Decide para qual nó o grafo segue depois da verificação de segurança.
 */
@Component
public class RouteAfterPromptSafetyCheck implements EdgeAction<SafeguardWorkflowState> {

    public static final String CHAT_ROUTE = "chat";
    public static final String BLOCKED_ROUTE = "blocked";

    @Override
    public String apply(SafeguardWorkflowState state) {
        if (!state.guardrailsEnabled()) {
            return CHAT_ROUTE;
        }

        GuardrailEvaluation evaluation = state.guardrailEvaluation();
        if (evaluation == null || evaluation.safe()) {
            return CHAT_ROUTE;
        }

        return BLOCKED_ROUTE;
    }
}
