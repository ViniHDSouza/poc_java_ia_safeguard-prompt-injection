package br.com.poc.safeguardpromptinjection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuração simples que define se o safeguard começa habilitado por padrão. */
@ConfigurationProperties(prefix = "app.guardrails")
public record GuardrailProperties(boolean enabledByDefault) {
}
