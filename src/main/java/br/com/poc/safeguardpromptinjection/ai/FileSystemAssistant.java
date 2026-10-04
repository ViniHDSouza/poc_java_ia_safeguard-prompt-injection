package br.com.poc.safeguardpromptinjection.ai;

/**
 * Interface simples que o LangChain4j implementa em tempo de execução.
 */
public interface FileSystemAssistant {
    String respondToUserMessage(String userMessage);
}
