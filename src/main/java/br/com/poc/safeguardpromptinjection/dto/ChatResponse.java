package br.com.poc.safeguardpromptinjection.dto;

/** Resposta didática que mostra também se o safeguard estava ativo e se bloqueou a requisição. */
public record ChatResponse(
        String username,
        String role,
        boolean guardrailsEnabled,
        boolean blocked,
        String guardrailAnalysis,
        String response
) {
}
