package br.com.poc.safeguardpromptinjection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração do servidor MCP Filesystem iniciado como processo filho via npx.
 */
@ConfigurationProperties(prefix = "app.mcp")
public record McpFileSystemProperties(
        String npxCommand,
        String filesystemPackage,
        String allowedDirectory,
        boolean logEvents
) {
}
