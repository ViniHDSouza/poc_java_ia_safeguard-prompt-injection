package br.com.poc.safeguardpromptinjection;

import br.com.poc.safeguardpromptinjection.config.GuardrailProperties;
import br.com.poc.safeguardpromptinjection.config.McpFileSystemProperties;
import br.com.poc.safeguardpromptinjection.config.OpenRouterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Ponto de entrada da POC em Spring Boot.
 * As propriedades externas ficam separadas para facilitar o estudo das configurações de IA e MCP.
 */
@SpringBootApplication
@EnableConfigurationProperties({
        OpenRouterProperties.class,
        GuardrailProperties.class,
        McpFileSystemProperties.class
})
public class SafeguardPromptInjectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeguardPromptInjectionApplication.class, args);
    }
}
