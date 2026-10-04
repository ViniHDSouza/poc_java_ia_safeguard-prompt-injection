package br.com.poc.safeguardpromptinjection.controller;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;
import br.com.poc.safeguardpromptinjection.dto.ChatRequest;
import br.com.poc.safeguardpromptinjection.dto.ChatResponse;
import br.com.poc.safeguardpromptinjection.repository.UserRepository;
import br.com.poc.safeguardpromptinjection.service.SafeguardChatWorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

/**
 * Endpoints REST da POC. O controller apenas recebe/retorna dados; a decisão de segurança fica no workflow.
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private final SafeguardChatWorkflowService safeguardChatWorkflowService;
    private final UserRepository userRepository;

    public ChatController(
            SafeguardChatWorkflowService safeguardChatWorkflowService,
            UserRepository userRepository
    ) {
        this.safeguardChatWorkflowService = safeguardChatWorkflowService;
        this.userRepository = userRepository;
    }

    @PostMapping("/chat")
    public ChatResponse processChatRequest(@Valid @RequestBody ChatRequest request) {
        return safeguardChatWorkflowService.processChatRequest(request);
    }

    @GetMapping("/users")
    public Collection<ApplicationUser> listAvailableUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/health")
    public Map<String, String> checkApplicationHealth() {
        return Map.of("status", "UP");
    }
}
