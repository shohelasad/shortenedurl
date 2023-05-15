package com.demo.app.shortenurl;

import com.demo.app.shortenurl.models.Role;
import com.demo.app.shortenurl.enums.UserRole;
import com.demo.app.shortenurl.payload.request.SignupRequest;
import com.demo.app.shortenurl.repository.RoleRepository;
import com.demo.app.shortenurl.services.AuthService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

  private final AuthService authService;

  private final RoleRepository roleRepository;

  public DatabaseInitializer(AuthService authService, RoleRepository roleRepository) {
    this.authService = authService;
    this.roleRepository = roleRepository;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    //save role
    List<Role> roles = new ArrayList<>();
    Role role = new Role();
    role.setUserRole(UserRole.ROLE_USER);
    roles.add(role);
    Role admin = new Role();
    admin.setUserRole(UserRole.ROLE_ADMIN);
    roles.add(admin);
    roleRepository.saveAll(roles);

    //register user
    SignupRequest signupRequest = new SignupRequest("user@email.com","123456");
    signupRequest.setRoles(new HashSet<>(Arrays.asList("ROLE_USER")));
    authService.registerUser(signupRequest);

    //register admin
    SignupRequest signupRequestAdmin = new SignupRequest("admin@email.com","123456");
    signupRequestAdmin.setRoles(new HashSet<>(Arrays.asList("ROLE_ADMIN")));
    authService.registerUser(signupRequestAdmin);
  }
}
