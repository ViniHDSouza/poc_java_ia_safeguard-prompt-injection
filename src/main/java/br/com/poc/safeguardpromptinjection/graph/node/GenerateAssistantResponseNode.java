package br.com.poc.safeguardpromptinjection.graph.node;

import br.com.poc.safeguardpromptinjection.graph.SafeguardWorkflowState;
import br.com.poc.safeguardpromptinjection.service.FileSystemAssistantService;
import org.bsc.langgraph4j.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Nó executado quando a mensagem pode seguir para o modelo principal com tools MCP. */
@Component
public class GenerateAssistantResponseNode implements NodeAction<SafeguardWorkflowState> {

    private static final Logger log = LoggerFactory.getLogger(GenerateAssistantResponseNode.class);

    private final FileSystemAssistantService fileSystemAssistantService;

    public GenerateAssistantResponseNode(FileSystemAssistantService fileSystemAssistantService) {
        this.fileSystemAssistantService = fileSystemAssistantService;
    }

    @Override
    public Map<String, Object> apply(SafeguardWorkflowState state) {
        try {
            String response = fileSystemAssistantService.generateResponse(
                    state.user(),
                    state.userPrompt()
            );
            return Map.of(SafeguardWorkflowState.ASSISTANT_RESPONSE, response);
        } catch (Exception exception) {
            log.error("Chat node failed", exception);
            return Map.of(
                    SafeguardWorkflowState.ASSISTANT_RESPONSE,
                    "I apologize, but I encountered an error processing your request. " +
                            "Check the application logs and configuration."
            );
        }
    }
}
