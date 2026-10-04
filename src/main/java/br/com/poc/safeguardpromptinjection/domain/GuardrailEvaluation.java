package br.com.poc.safeguardpromptinjection.domain;

import java.io.Serializable;

/**
 * Resultado da segunda IA, usada somente como camada de segurança.
 */
public record GuardrailEvaluation(
        boolean safe,
        String reason,
        String analysis
) implements Serializable {
    public static GuardrailEvaluation guardrailsDisabled() {
        return new GuardrailEvaluation(true, "Guardrails disabled", "Guardrails disabled");
    }

    public static GuardrailEvaluation blockedBecauseServiceFailed(String details) {
        return new GuardrailEvaluation(
                false,
                "Guardrails service unavailable - request blocked for safety",
                details
        );
    }
}
