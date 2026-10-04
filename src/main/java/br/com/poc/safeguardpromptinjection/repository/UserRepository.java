package br.com.poc.safeguardpromptinjection.repository;

import br.com.poc.safeguardpromptinjection.domain.ApplicationUser;

import java.util.Collection;
import java.util.Optional;

/** Abstração pequena para não acoplar o controller ao arquivo JSON. */
public interface UserRepository {
    Optional<ApplicationUser> findByUsername(String username);
    Collection<ApplicationUser> findAll();
}
