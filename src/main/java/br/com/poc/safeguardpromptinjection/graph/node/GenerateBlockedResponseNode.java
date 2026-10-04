package br.com.poc.safeguardpromptinjection.graph.node;

import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import br.com.poc.safeguardpromptinjection.graph.SafeguardWorkflowState;
import br.com.poc.safeguardpromptinjection.service.PromptTemplateRenderer;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Nó que gera a mensagem de bloqueio sem chamar o agente com acesso ao filesystem. */
@Component
public class GenerateBlockedResponseNode implements NodeAction<SafeguardWorkflowState> {

    private final PromptTemplateRenderer promptTemplateRenderer;

    public GenerateBlockedResponseNode(PromptTemplateRenderer promptTemplateRenderer) {
        this.promptTemplateRenderer = promptTemplateRenderer;
    }

    @Override
    public Map<String, Object> apply(SafeguardWorkflowState state) {
        GuardrailEvaluation evaluation = state.guardrailEvaluation();

        String blockedMessage = promptTemplateRenderer.renderBlockedMessage(
                state.user(),
                evaluation == null ? "Security check failed" : evaluation.reason(),
                evaluation == null ? "" : evaluation.analysis()
        );

        return Map.of(SafeguardWorkflowState.ASSISTANT_RESPONSE, blockedMessage);
    }
}
