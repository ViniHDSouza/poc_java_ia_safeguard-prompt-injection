package br.com.poc.safeguardpromptinjection.service;

import br.com.poc.safeguardpromptinjection.ai.FileSystemAssistant;
import br.com.poc.safeguardpromptinjection.config.OpenRouterProperties;
import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Executa o modelo principal. Este modelo recebe as tools MCP e pode decidir chamá-las.
 */
@Service
public class FileSystemAssistantService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemAssistantService.class);

    private final ChatModel mainChatModel;
    private final OpenRouterProperties openRouterProperties;
    private final FileSystemMcpToolProviderFactory mcpToolProviderFactory;
    private final PromptTemplateRenderer promptTemplateRenderer;

    public FileSystemAssistantService(
            @Qualifier("mainChatModel") ChatModel mainChatModel,
            OpenRouterProperties openRouterProperties,
            FileSystemMcpToolProviderFactory mcpToolProviderFactory,
            PromptTemplateRenderer promptTemplateRenderer
    ) {
        this.mainChatModel = mainChatModel;
        this.openRouterProperties = openRouterProperties;
        this.mcpToolProviderFactory = mcpToolProviderFactory;
        this.promptTemplateRenderer = promptTemplateRenderer;
    }

    public String generateResponse(ApplicationUser user, String userPrompt) {
        ensureOpenRouterApiKeyWasConfigured();

        String systemPrompt = promptTemplateRenderer.renderSystemPromptFor(user);
        log.info("Calling main model for user={} role={}", user.username(), user.role());

        // Criamos o AI Service com o system prompt já renderizado para o usuário atual.
        // O ToolProvider vem do servidor MCP Filesystem.
        FileSystemAssistant assistant = AiServices.builder(FileSystemAssistant.class)
                .chatModel(mainChatModel)
                .systemMessage(systemPrompt)
                .toolProvider(mcpToolProviderFactory.getOrCreateToolProvider())
                .build();

        return assistant.respondToUserMessage(userPrompt);
    }

    private void ensureOpenRouterApiKeyWasConfigured() {
        if (openRouterProperties.apiKey() == null || openRouterProperties.apiKey().isBlank()) {
            throw new IllegalStateException(
                    "OPENROUTER_API_KEY is not configured. Copy .env.example to .env and configure the key."
            );
        }
    }
}
