package br.com.poc.safeguardpromptinjection.dto;

import jakarta.validation.constraints.NotBlank;

/** Entrada da API. `guardrailsEnabled` permite comparar conscientemente o modo seguro e o inseguro. */
public record ChatRequest(
        @NotBlank String username,
        @NotBlank String message,
        Boolean guardrailsEnabled
) {
}
