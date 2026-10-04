package br.com.poc.safeguardpromptinjection.graph;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import br.com.poc.safeguardpromptinjection.domain.GuardrailEvaluation;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.Map;

/**
 * Estado compartilhado entre os nós do LangGraph4j.
 * Ele é equivalente ao GraphState do exemplo TypeScript.
 */
public class SafeguardWorkflowState extends AgentState {

    public static final String USER = "user";
    public static final String USER_PROMPT = "userPrompt";
    public static final String GUARDRAILS_ENABLED = "guardrailsEnabled";
    public static final String GUARDRAIL_EVALUATION = "guardrailEvaluation";
    public static final String ASSISTANT_RESPONSE = "assistantResponse";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            USER, Channels.base(() -> null),
            USER_PROMPT, Channels.base(() -> ""),
            GUARDRAILS_ENABLED, Channels.base(() -> true),
            GUARDRAIL_EVALUATION, Channels.base(() -> null),
            ASSISTANT_RESPONSE, Channels.base(() -> "")
    );

    public SafeguardWorkflowState(Map<String, Object> initialData) {
        super(initialData);
    }

    public ApplicationUser user() {
        return this.<ApplicationUser>value(USER)
                .orElseThrow(() -> new IllegalStateException("User is missing from graph state"));
    }

    public String userPrompt() {
        return this.<String>value(USER_PROMPT).orElse("");
    }

    public boolean guardrailsEnabled() {
        return this.<Boolean>value(GUARDRAILS_ENABLED).orElse(true);
    }

    public GuardrailEvaluation guardrailEvaluation() {
        return this.<GuardrailEvaluation>value(GUARDRAIL_EVALUATION).orElse(null);
    }

    public String assistantResponse() {
        return this.<String>value(ASSISTANT_RESPONSE).orElse("");
    }
}
