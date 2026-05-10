package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.Role;
import com.silenthelp.silenthelp.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
