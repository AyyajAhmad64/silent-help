package com.silenthelp.silenthelp.config;

import com.silenthelp.silenthelp.dto.HelpRequestForm;
import com.silenthelp.silenthelp.dto.ResponseForm;
import com.silenthelp.silenthelp.model.Category;
import com.silenthelp.silenthelp.model.AccountStatus;
import com.silenthelp.silenthelp.model.Role;
import com.silenthelp.silenthelp.model.RoleName;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.CategoryRepository;
import com.silenthelp.silenthelp.repository.HelpRequestRepository;
import com.silenthelp.silenthelp.repository.RoleRepository;
import com.silenthelp.silenthelp.repository.UserRepository;
import com.silenthelp.silenthelp.service.HelpRequestService;
import com.silenthelp.silenthelp.service.ResponseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               CategoryRepository categoryRepository,
                               UserRepository userRepository,
                               HelpRequestRepository helpRequestRepository,
                               PasswordEncoder passwordEncoder,
                               HelpRequestService helpRequestService,
                               ResponseService responseService,
                               @Value("${app.seed.enabled:true}") boolean seedEnabled,
                               @Value("${app.default-admin.username:admin}") String adminUsername,
                               @Value("${app.default-admin.password:change-me}") String adminPassword,
                               @Value("${app.policy.version:2026-05}") String policyVersion) {
        return args -> {
            if (!seedEnabled) {
                return;
            }
            seedRoles(roleRepository);
            seedCategories(categoryRepository);
            User admin = seedUser(userRepository, roleRepository, passwordEncoder, adminUsername,
                    "realadmin@gmail.com", adminPassword, "System Admin", RoleName.ADMIN, policyVersion);
            User student = seedUser(userRepository, roleRepository, passwordEncoder, "student",
                    "student@college.edu", "Student@2026", "Demo Student", RoleName.STUDENT, policyVersion);
            if (helpRequestRepository.count() == 0) {
                createSampleRequests(categoryRepository, helpRequestService, responseService, student, admin);
            }
        };
    }

    private void seedRoles(RoleRepository roleRepository) {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }

    private void seedCategories(CategoryRepository categoryRepository) {
        List<Category> categories = List.of(
                new Category("Academic Help", "academic-help", "bi-mortarboard", "#2563eb"),
                new Category("Notes Request", "notes-request", "bi-journal-text", "#0891b2"),
                new Category("Placement Guidance", "placement-guidance", "bi-briefcase", "#4f46e5"),
                new Category("Financial Help", "financial-help", "bi-wallet2", "#16a34a"),
                new Category("Emotional Support", "emotional-support", "bi-chat-heart", "#db2777"),
                new Category("Lost & Found", "lost-found", "bi-search", "#f59e0b"),
                new Category("Hostel Problems", "hostel-problems", "bi-building", "#475569"),
                new Category("Technical Problems", "technical-problems", "bi-cpu", "#0f766e")
        );
        categories.stream()
                .filter(category -> !categoryRepository.existsBySlug(category.getSlug()))
                .forEach(categoryRepository::save);
    }

    private User seedUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          String username, String email, String password, String displayName, RoleName roleName,
                          String policyVersion) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        return userRepository.findByUsername(username).map(user -> {
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setDisplayName(displayName);
            user.setEnabled(true);
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setEnrollmentNumber(roleName == RoleName.STUDENT ? "DEMO-STUDENT-001" : "DEMO-ADMIN-001");
            user.setVerifiedStudent(true);
            user.setVerifiedByAdmin(true);
            user.setVerifiedAt(LocalDateTime.now());
            user.setEmailVerified(true);
            user.setEmailVerifiedAt(LocalDateTime.now());
            user.setTermsAccepted(true);
            user.setTermsAcceptedAt(LocalDateTime.now());
            user.setPolicyVersion(policyVersion);
            if (user.isDeleted()) {
                user.setDeleted(false);
                user.setDeletionReason(null);
                user.setDeletedAt(null);
                user.setReactivatedAt(LocalDateTime.now());
            }
            user.getRoles().add(role);
            return userRepository.save(user);
        }).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setDisplayName(displayName);
            user.setDepartment("Computer Applications");
            user.setYearOfStudy("Final Year");
            user.setEnrollmentNumber(roleName == RoleName.STUDENT ? "DEMO-STUDENT-001" : "DEMO-ADMIN-001");
            user.setVerifiedStudent(true);
            user.setVerifiedByAdmin(true);
            user.setVerifiedAt(LocalDateTime.now());
            user.setEmailVerified(true);
            user.setEmailVerifiedAt(LocalDateTime.now());
            user.setTermsAccepted(true);
            user.setTermsAcceptedAt(LocalDateTime.now());
            user.setPolicyVersion(policyVersion);
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.getRoles().add(role);
            return userRepository.save(user);
        });
    }

    private void createSampleRequests(CategoryRepository categoryRepository, HelpRequestService helpRequestService,
                                      ResponseService responseService, User student, User admin) {
        Category technical = categoryRepository.findBySlug("technical-problems").orElseThrow();
        Category placement = categoryRepository.findBySlug("placement-guidance").orElseThrow();
        HelpRequestForm first = new HelpRequestForm();
        first.setTitle("Need help debugging Spring Security login redirect");
        first.setDescription("I am building a Java project and my login succeeds, but the dashboard route keeps redirecting. Can someone review what I might be missing?");
        first.setCategoryId(technical.getId());
        first.setAnonymous(true);
        var saved = helpRequestService.create(first, student);

        ResponseForm response = new ResponseForm();
        response.setMessage("Check whether your SecurityFilterChain allows the login page and whether your roles include the ROLE_ prefix at runtime.");
        response.setAnonymous(true);
        responseService.respond(saved.getId(), response, admin);

        HelpRequestForm second = new HelpRequestForm();
        second.setTitle("Looking for MCA placement preparation roadmap");
        second.setDescription("I want a practical weekly plan for aptitude, Java, SQL, and interview preparation before campus drives begin.");
        second.setCategoryId(placement.getId());
        second.setAnonymous(true);
        helpRequestService.create(second, student);
    }
}
