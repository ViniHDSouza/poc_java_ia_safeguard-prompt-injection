package br.com.poc.safeguardpromptinjection.graph.node;

import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import br.com.poc.safeguardpromptinjection.graph.SafeguardWorkflowState;
import br.com.poc.safeguardpromptinjection.service.PromptInjectionSafeguardService;
import br.com.poc.safeguardpromptinjection.service.PromptTemplateRenderer;
import org.bsc.langgraph4j.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Primeiro nó: monta o contexto e pede ao modelo safeguard para classificar a entrada. */
@Component
public class EvaluatePromptSafetyNode implements NodeAction<SafeguardWorkflowState> {

    private static final Logger log = LoggerFactory.getLogger(EvaluatePromptSafetyNode.class);

    private final PromptInjectionSafeguardService safeguardService;
    private final PromptTemplateRenderer promptTemplateRenderer;

    public EvaluatePromptSafetyNode(
            PromptInjectionSafeguardService safeguardService,
            PromptTemplateRenderer promptTemplateRenderer
    ) {
        this.safeguardService = safeguardService;
        this.promptTemplateRenderer = promptTemplateRenderer;
    }

    @Override
    public Map<String, Object> apply(SafeguardWorkflowState state) {
        try {
            String systemPrompt = promptTemplateRenderer.renderSystemPromptFor(state.user());

            // Assim como na aula, o safeguard recebe o contexto de segurança + a mensagem do usuário.
            String completeInputForSecurityAnalysis = systemPrompt + "\n" + state.userPrompt();

            GuardrailEvaluation evaluation = safeguardService.evaluatePromptSafety(
                    completeInputForSecurityAnalysis,
                    state.guardrailsEnabled()
            );

            log.info("Guardrail result: safe={} analysis={}", evaluation.safe(), evaluation.analysis());
            return Map.of(SafeguardWorkflowState.GUARDRAIL_EVALUATION, evaluation);
        } catch (Exception exception) {
            // Fail closed: se a camada de segurança falhar, a requisição é bloqueada.
            log.error("Guardrails check failed. Blocking request for safety.", exception);
            GuardrailEvaluation failedEvaluation = GuardrailEvaluation.blockedBecauseServiceFailed(
                    exception.getMessage()
            );
            return Map.of(SafeguardWorkflowState.GUARDRAIL_EVALUATION, failedEvaluation);
        }
    }
}
