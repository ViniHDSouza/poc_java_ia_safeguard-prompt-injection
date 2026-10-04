package br.com.poc.safeguardpromptinjection.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Centraliza a leitura dos prompts mantidos em arquivos de resources. */
@Component
public class PromptResourceLoader {

    public String loadPromptFromClasspath(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load prompt: " + classpathLocation, exception);
        }
    }
}
