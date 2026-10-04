package br.com.poc.safeguardpromptinjection.service;

import br.com.poc.safeguardpromptinjection.config.McpFileSystemProperties;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * Cria o MCP somente quando uma requisição realmente chega ao nó de chat.
 * Assim, uma requisição bloqueada pelo safeguard nem chega a iniciar o servidor filesystem.
 */
@Component
public class FileSystemMcpToolProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(FileSystemMcpToolProviderFactory.class);

    private final McpFileSystemProperties properties;
    private McpClient mcpClient;
    private ToolProvider toolProvider;

    public FileSystemMcpToolProviderFactory(McpFileSystemProperties properties) {
        this.properties = properties;
    }

    public synchronized ToolProvider getOrCreateToolProvider() {
        if (toolProvider != null) {
            return toolProvider;
        }

        Path allowedDirectory = Path.of(properties.allowedDirectory()).toAbsolutePath().normalize();
        String npxCommand = resolveNpxCommandForCurrentOperatingSystem();
        log.info("Starting MCP Filesystem. Allowed directory: {}", allowedDirectory);

        McpTransport transport = StdioMcpTransport.builder()
                .command(List.of(
                        npxCommand,
                        "-y",
                        properties.filesystemPackage(),
                        allowedDirectory.toString()
                ))
                .logEvents(properties.logEvents())
                .build();

        this.mcpClient = DefaultMcpClient.builder()
                .key("filesystem-mcp")
                .transport(transport)
                .build();

        // IMPORTANTE PARA A DEMONSTRAÇÃO:
        // não filtramos tools por role aqui. O projeto original também deixava as tools
        // disponíveis ao agente e confiava nas instruções do system prompt.
        // É isso que permite estudar o risco de Prompt Injection no modo inseguro.
        this.toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .failIfOneServerFails(true)
                .build();

        return toolProvider;
    }

    private String resolveNpxCommandForCurrentOperatingSystem() {
        String configuredCommand = properties.npxCommand();
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (isWindows && "npx".equalsIgnoreCase(configuredCommand)) {
            return "npx.cmd";
        }
        return configuredCommand;
    }

    @PreDestroy
    public synchronized void closeMcpClient() {
        if (mcpClient != null) {
            log.info("Closing MCP Filesystem client");
            mcpClient.close();
            mcpClient = null;
            toolProvider = null;
        }
    }
}
