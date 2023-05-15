package com.demo.app.shortenurl.repository;

import java.util.Optional;

import com.demo.app.shortenurl.enums.UserRole;
import com.demo.app.shortenurl.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByUserRole(UserRole userRole);
}
