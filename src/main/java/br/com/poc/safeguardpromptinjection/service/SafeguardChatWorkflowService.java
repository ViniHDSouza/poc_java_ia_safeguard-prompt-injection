package br.com.poc.safeguardpromptinjection.service;

import br.com.poc.safeguardpromptinjection.config.GuardrailProperties;
import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import br.com.poc.safeguardpromptinjection.dto.ChatRequest;
import br.com.poc.safeguardpromptinjection.dto.ChatResponse;
import br.com.poc.safeguardpromptinjection.graph.SafeguardWorkflowState;
import br.com.poc.safeguardpromptinjection.repository.UserRepository;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphInput;
import org.bsc.langgraph4j.RunnableConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Orquestra a entrada REST: resolve usuário, define modo seguro/inseguro, executa o grafo e monta a resposta.
 */
@Service
public class SafeguardChatWorkflowService {

    private final UserRepository userRepository;
    private final GuardrailProperties guardrailProperties;
    private final CompiledGraph<SafeguardWorkflowState> safeguardWorkflow;

    public SafeguardChatWorkflowService(
            UserRepository userRepository,
            GuardrailProperties guardrailProperties,
            CompiledGraph<SafeguardWorkflowState> safeguardWorkflow
    ) {
        this.userRepository = userRepository;
        this.guardrailProperties = guardrailProperties;
        this.safeguardWorkflow = safeguardWorkflow;
    }

    public ChatResponse processChatRequest(ChatRequest request) {
        ApplicationUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unknown user: " + request.username()
                ));

        boolean guardrailsEnabled = request.guardrailsEnabled() == null
                ? guardrailProperties.enabledByDefault()
                : request.guardrailsEnabled();

        Map<String, Object> initialState = new HashMap<>();
        initialState.put(SafeguardWorkflowState.USER, user);
        initialState.put(SafeguardWorkflowState.USER_PROMPT, request.message());
        initialState.put(SafeguardWorkflowState.GUARDRAILS_ENABLED, guardrailsEnabled);

        SafeguardWorkflowState finalState = safeguardWorkflow
                .invoke(GraphInput.args(initialState), RunnableConfig.empty())
                .orElseThrow(() -> new IllegalStateException("The LangGraph4j workflow produced no final state"));

        GuardrailEvaluation evaluation = finalState.guardrailEvaluation();
        boolean blocked = guardrailsEnabled && evaluation != null && !evaluation.safe();

        return new ChatResponse(
                user.username(),
                user.role(),
                guardrailsEnabled,
                blocked,
                evaluation == null ? null : evaluation.analysis(),
                finalState.assistantResponse()
        );
    }
}
