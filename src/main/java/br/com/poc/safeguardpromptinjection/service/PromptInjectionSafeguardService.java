package br.com.poc.safeguardpromptinjection.service;

import br.com.poc.safeguardpromptinjection.config.OpenRouterProperties;
import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import dev.langchain4j.model.chat.ChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Segunda IA da aplicação. Ela NÃO recebe tools MCP.
 * Sua única função é dizer se a entrada aparenta ser SAFE ou UNSAFE.
 */
@Service
public class PromptInjectionSafeguardService {

    private static final Logger log = LoggerFactory.getLogger(PromptInjectionSafeguardService.class);

    private final ChatModel safeguardChatModel;
    private final OpenRouterProperties openRouterProperties;
    private final PromptTemplateRenderer promptTemplateRenderer;

    public PromptInjectionSafeguardService(
            @Qualifier("safeguardChatModel") ChatModel safeguardChatModel,
            OpenRouterProperties openRouterProperties,
            PromptTemplateRenderer promptTemplateRenderer
    ) {
        this.safeguardChatModel = safeguardChatModel;
        this.openRouterProperties = openRouterProperties;
        this.promptTemplateRenderer = promptTemplateRenderer;
    }

    public GuardrailEvaluation evaluatePromptSafety(String inputToAnalyze, boolean guardrailsEnabled) {
        if (!guardrailsEnabled) {
            return GuardrailEvaluation.guardrailsDisabled();
        }

        ensureOpenRouterApiKeyWasConfigured();
        String safeguardPrompt = promptTemplateRenderer.renderGuardrailPrompt(inputToAnalyze);

        log.info("Checking request with safeguard model={}", openRouterProperties.guardrailModel());
        String modelAnalysis = safeguardChatModel.chat(safeguardPrompt).trim();
        boolean unsafe = modelAnalysis.toUpperCase().startsWith("UNSAFE");

        if (unsafe) {
            return new GuardrailEvaluation(
                    false,
                    "Prompt Injection detected by safeguard model",
                    modelAnalysis
            );
        }

        return new GuardrailEvaluation(true, "Safe request", modelAnalysis);
    }

    private void ensureOpenRouterApiKeyWasConfigured() {
        if (openRouterProperties.apiKey() == null || openRouterProperties.apiKey().isBlank()) {
            throw new IllegalStateException(
                    "OPENROUTER_API_KEY is not configured. Copy .env.example to .env and configure the key."
            );
        }
    }
}
