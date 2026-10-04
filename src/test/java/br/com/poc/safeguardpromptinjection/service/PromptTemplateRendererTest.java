package br.com.poc.safeguardpromptinjection.service;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromptTemplateRendererTest {

    private final PromptTemplateRenderer renderer = new PromptTemplateRenderer(new PromptResourceLoader());

    @Test
    void shouldRenderCurrentUserInsideSystemPrompt() {
        ApplicationUser user = new ApplicationUser("ananeri", "member", List.of(), "Ana Neri");

        String prompt = renderer.renderSystemPromptFor(user);

        assertThat(prompt).contains("Ana Neri");
        assertThat(prompt).contains("member");
        assertThat(prompt).doesNotContain("{{USER_NAME}}");
    }

    @Test
    void shouldRenderBlockedMessageWithRoleAndReason() {
        ApplicationUser user = new ApplicationUser("ananeri", "member", List.of(), "Ana Neri");

        String message = renderer.renderBlockedMessage(user, "Prompt Injection detected", "UNSAFE override attempt");

        assertThat(message).contains("Prompt Injection detected");
        assertThat(message).contains("member");
        assertThat(message).contains("UNSAFE override attempt");
    }
}
