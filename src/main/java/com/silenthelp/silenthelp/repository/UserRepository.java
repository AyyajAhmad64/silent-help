package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRolesContaining(Role role);

    Page<User> findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String displayName, String email, Pageable pageable);

    Page<User> findByRolesNameAndUsernameContainingIgnoreCaseOrRolesNameAndDisplayNameContainingIgnoreCaseOrRolesNameAndEmailContainingIgnoreCase(
            com.silenthelp.silenthelp.model.RoleName usernameRole, String username,
            com.silenthelp.silenthelp.model.RoleName displayNameRole, String displayName,
            com.silenthelp.silenthelp.model.RoleName emailRole, String email,
            Pageable pageable);

    Page<User> findDistinctByRolesName(com.silenthelp.silenthelp.model.RoleName roleName, Pageable pageable);

    List<User> findDistinctByRolesName(com.silenthelp.silenthelp.model.RoleName roleName);

    Page<User> findDistinctByRolesNameAndDeletedTrue(com.silenthelp.silenthelp.model.RoleName roleName, Pageable pageable);

    long countDistinctByRolesNameAndDeletedTrue(com.silenthelp.silenthelp.model.RoleName roleName);
}
