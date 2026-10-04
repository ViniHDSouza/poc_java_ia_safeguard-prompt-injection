package br.com.poc.safeguardpromptinjection.service;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Usa PromptTemplate do LangChain4j em vez de fazer replace manual de String.
 * Esse ponto corresponde à aula que troca substituições manuais por templates.
 */
@Component
public class PromptTemplateRenderer {

    private final PromptResourceLoader promptResourceLoader;

    public PromptTemplateRenderer(PromptResourceLoader promptResourceLoader) {
        this.promptResourceLoader = promptResourceLoader;
    }

    public String renderSystemPromptFor(ApplicationUser user) {
        String templateText = promptResourceLoader.loadPromptFromClasspath("prompts/system.txt");
        return PromptTemplate.from(templateText)
                .apply(Map.of(
                        "USER_NAME", user.displayName(),
                        "USER_ROLE", user.role()
                ))
                .text();
    }

    public String renderGuardrailPrompt(String inputToAnalyze) {
        String templateText = promptResourceLoader.loadPromptFromClasspath("prompts/guardrails.txt");
        return PromptTemplate.from(templateText)
                .apply(Map.of("USER_INPUT", inputToAnalyze))
                .text();
    }

    public String renderBlockedMessage(
            ApplicationUser user,
            String reason,
            String analysis
    ) {
        String templateText = promptResourceLoader.loadPromptFromClasspath("prompts/blocked.txt");
        String formattedAnalysis = analysis == null || analysis.isBlank()
                ? ""
                : "**Analysis:** " + analysis;

        return PromptTemplate.from(templateText)
                .apply(Map.of(
                        "REASON", reason == null ? "Security check failed" : reason,
                        "ANALYSIS", formattedAnalysis,
                        "USER_ROLE", user.role(),
                        "PERMISSIONS", user.permissionsAsText()
                ))
                .text();
    }
}
