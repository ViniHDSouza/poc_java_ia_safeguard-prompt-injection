package br.com.poc.safeguardpromptinjection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configura os dois modelos usados na POC:
 * 1) o modelo principal, que conversa e pode chamar tools;
 * 2) o modelo safeguard, que classifica a entrada antes do modelo principal.
 */
@ConfigurationProperties(prefix = "app.openrouter")
public record OpenRouterProperties(
        String baseUrl,
        String apiKey,
        String httpReferer,
        String xTitle,
        String chatModel,
        String guardrailModel,
        Double temperature,
        Integer maxTokens
) {
}
