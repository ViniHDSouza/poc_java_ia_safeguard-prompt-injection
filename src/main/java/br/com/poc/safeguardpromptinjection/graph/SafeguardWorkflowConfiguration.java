package br.com.poc.safeguardpromptinjection.graph;

import br.com.poc.safeguardpromptinjection.graph.node.EvaluatePromptSafetyNode;
import br.com.poc.safeguardpromptinjection.graph.node.GenerateAssistantResponseNode;
import br.com.poc.safeguardpromptinjection.graph.node.GenerateBlockedResponseNode;
import br.com.poc.safeguardpromptinjection.graph.routing.RouteAfterPromptSafetyCheck;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * Reproduz em LangGraph4j o fluxo do projeto original:
 * START -> guardrail_check -> chat|blocked -> END.
 */
@Configuration
public class SafeguardWorkflowConfiguration {

    public static final String GUARDRAIL_NODE = "guardrail_check";
    public static final String CHAT_NODE = "chat";
    public static final String BLOCKED_NODE = "blocked";

    @Bean
    public CompiledGraph<SafeguardWorkflowState> safeguardWorkflow(
            EvaluatePromptSafetyNode evaluatePromptSafetyNode,
            GenerateAssistantResponseNode generateAssistantResponseNode,
            GenerateBlockedResponseNode generateBlockedResponseNode,
            RouteAfterPromptSafetyCheck routeAfterPromptSafetyCheck
    ) throws GraphStateException {

        StateGraph<SafeguardWorkflowState> workflow = new StateGraph<>(
                SafeguardWorkflowState.SCHEMA,
                SafeguardWorkflowState::new
        );

        workflow
                .addNode(GUARDRAIL_NODE, node_async(evaluatePromptSafetyNode))
                .addNode(CHAT_NODE, node_async(generateAssistantResponseNode))
                .addNode(BLOCKED_NODE, node_async(generateBlockedResponseNode))
                .addEdge(START, GUARDRAIL_NODE)
                .addConditionalEdges(
                        GUARDRAIL_NODE,
                        edge_async(routeAfterPromptSafetyCheck),
                        Map.of(
                                RouteAfterPromptSafetyCheck.CHAT_ROUTE, CHAT_NODE,
                                RouteAfterPromptSafetyCheck.BLOCKED_ROUTE, BLOCKED_NODE
                        )
                )
                .addEdge(CHAT_NODE, END)
                .addEdge(BLOCKED_NODE, END);

        return workflow.compile();
    }
}
