package br.com.poc.safeguardpromptinjection.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Usuário didático carregado do JSON original.
 * A lista de permissões é exibida no prompt/retorno, mas não constitui RBAC real nas tools MCP.
 */
public record ApplicationUser(
        String username,
        String role,
        List<String> permissions,
        String displayName
) implements Serializable {
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public String permissionsAsText() {
        return permissions == null || permissions.isEmpty()
                ? "None"
                : String.join(", ", permissions);
    }
}
