package br.com.poc.safeguardpromptinjection.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cria os dois clientes de IA usados na POC apontando a API OpenAI-compatible para o OpenRouter.
 * O modelo principal recebe tools em outra camada; o safeguard nunca recebe tools.
 */
@Configuration
public class OpenRouterModelConfiguration {

    @Bean
    @Qualifier("mainChatModel")
    public ChatModel createMainChatModel(OpenRouterProperties properties) {
        return createOpenRouterChatModel(
                properties,
                properties.chatModel(),
                properties.temperature(),
                properties.maxTokens()
        );
    }

    @Bean
    @Qualifier("safeguardChatModel")
    public ChatModel createSafeguardChatModel(OpenRouterProperties properties) {
        // Mantemos a mesma temperatura do projeto JavaScript original para preservar
        // o comportamento da POC durante a migração.
        return createOpenRouterChatModel(
                properties,
                properties.guardrailModel(),
                properties.temperature(),
                properties.maxTokens()
        );
    }

    private ChatModel createOpenRouterChatModel(
            OpenRouterProperties properties,
            String modelName,
            Double temperature,
            Integer maxTokens
    ) {
        String apiKey = hasText(properties.apiKey()) ? properties.apiKey() : "OPENROUTER_API_KEY_NOT_CONFIGURED";

        return OpenAiChatModel.builder()
                .baseUrl(properties.baseUrl())
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .customHeaders(createOpenRouterHeaders(properties))
                .customParameters(createOpenRouterRequestParameters(properties))
                .logRequests(false)
                .logResponses(false)
                .build();
    }

    private Map<String, String> createOpenRouterHeaders(OpenRouterProperties properties) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (hasText(properties.httpReferer())) {
            headers.put("HTTP-Referer", properties.httpReferer());
        }
        if (hasText(properties.xTitle())) {
            headers.put("X-Title", properties.xTitle());
        }
        return headers;
    }

    /**
     * Preserva os modelKwargs do exemplo JavaScript.
     *
     * O código original enviava:
     * - models: lista com o modelo principal;
     * - provider.sort.by: price;
     * - provider.sort.partition: none.
     *
     * OpenRouter aceita esses campos no corpo da requisição e o LangChain4j
     * permite repassá-los por customParameters.
     */
    private Map<String, Object> createOpenRouterRequestParameters(OpenRouterProperties properties) {
        return Map.of(
                "models", List.of(properties.chatModel()),
                "provider", Map.of(
                        "sort", Map.of(
                                "by", "price",
                                "partition", "none"
                        )
                )
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
