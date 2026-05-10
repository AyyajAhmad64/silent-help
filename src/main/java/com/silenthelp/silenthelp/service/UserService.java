package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.dto.RegistrationForm;
import com.silenthelp.silenthelp.model.Role;
import com.silenthelp.silenthelp.model.PasswordResetToken;
import com.silenthelp.silenthelp.model.RoleName;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.PasswordResetTokenRepository;
import com.silenthelp.silenthelp.repository.RoleRepository;
import com.silenthelp.silenthelp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final String allowedEmailDomains;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder,
                       @Value("${app.registration.allowed-email-domains:edu,edu.in,ac.in}") String allowedEmailDomains) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.allowedEmailDomains = allowedEmailDomains;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                        .toList())
                .build();
    }

    @Transactional
    public void registerStudent(RegistrationForm form) {
        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();
        if (!isAllowedCollegeEmail(email)) {
            throw new IllegalArgumentException("Only college email addresses are allowed. Use your official .edu, .edu.in, or .ac.in email.");
        }

        Optional<User> existingUserOpt = userRepository.findByEmailIgnoreCase(email);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (!existingUser.isDeleted()) {
                throw new IllegalArgumentException("Email is already registered.");
            }
            // Reactivate deleted account
            existingUser.setDeleted(false);
            existingUser.setEnabled(true);
            existingUser.setDeletionReason(null);
            existingUser.setDeletedAt(null);
            existingUser.setReactivatedAt(LocalDateTime.now());
            existingUser.setUsername(username);
            existingUser.setPassword(passwordEncoder.encode(form.getPassword()));
            existingUser.setDisplayName(form.getDisplayName().trim());
            existingUser.setDepartment(form.getDepartment());
            existingUser.setYearOfStudy(form.getYearOfStudy());
            userRepository.save(existingUser);
            return;
        }

        // Check username only if not reactivating
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        Role role = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new IllegalStateException("Student role is not seeded."));
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setDisplayName(form.getDisplayName().trim());
        user.setDepartment(form.getDepartment());
        user.setYearOfStudy(form.getYearOfStudy());
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean adminExists() {
        return roleRepository.findByName(RoleName.ADMIN)
                .map(userRepository::existsByRolesContaining)
                .orElse(false);
    }

    @Transactional
    public void registerFirstAdmin(RegistrationForm form) {
        if (adminExists()) {
            throw new IllegalArgumentException("Admin setup is already completed.");
        }
        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();
        if (!isAllowedCollegeEmail(email)) {
            throw new IllegalArgumentException("Use an official college email for admin setup.");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        Role role = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Admin role is not seeded."));
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setDisplayName(form.getDisplayName().trim());
        user.setDepartment(form.getDepartment());
        user.setYearOfStudy(form.getYearOfStudy());
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private boolean isAllowedCollegeEmail(String email) {
        return java.util.Arrays.stream(allowedEmailDomains.split(","))
                .map(String::trim)
                .filter(domain -> !domain.isBlank())
                .anyMatch(domain -> email.endsWith("@" + domain) || email.endsWith("." + domain));
    }

    @Transactional(readOnly = true)
    public User requireByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void updateProfile(String username, String displayName, String department, String yearOfStudy) {
        User user = requireByUsername(username);
        user.setDisplayName(displayName);
        user.setDepartment(department);
        user.setYearOfStudy(yearOfStudy);
    }

    @Transactional
    public void setEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.isDeleted() && !enabled) {
            return;
        }
        user.setEnabled(enabled);
    }

    @Transactional
    public User deleteOwnAccount(String username, String reason) {
        if (reason == null || reason.trim().length() < 10) {
            throw new IllegalArgumentException("Please share a reason with at least 10 characters.");
        }
        User user = requireByUsername(username);
        if (user.hasRole(RoleName.ADMIN)) {
            throw new IllegalArgumentException("Admin accounts cannot be deleted from this page.");
        }
        user.setDeleted(true);
        user.setEnabled(false);
        user.setDeletionReason(reason.trim());
        user.setDeletedAt(LocalDateTime.now());
        return user;
    }

    @Transactional
    public User reactivateDeletedAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setDeleted(false);
        user.setEnabled(true);
        user.setDeletionReason(null);
        user.setDeletedAt(null);
        user.setReactivatedAt(LocalDateTime.now());
        return user;
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return userRepository.findDistinctByRolesName(RoleName.STUDENT, pageable);
        }
        String value = keyword.trim();
        return userRepository.findByRolesNameAndUsernameContainingIgnoreCaseOrRolesNameAndDisplayNameContainingIgnoreCaseOrRolesNameAndEmailContainingIgnoreCase(
                RoleName.STUDENT, value, RoleName.STUDENT, value, RoleName.STUDENT, value, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> deletedUsers(Pageable pageable) {
        return userRepository.findDistinctByRolesNameAndDeletedTrue(RoleName.STUDENT, pageable);
    }

    @Transactional(readOnly = true)
    public long deletedStudentCount() {
        return userRepository.countDistinctByRolesNameAndDeletedTrue(RoleName.STUDENT);
    }

    @Transactional(readOnly = true)
    public List<User> admins() {
        return userRepository.findDistinctByRolesName(RoleName.ADMIN);
    }

    @Transactional(readOnly = true)
    public String loginStatusCode(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            return "error";
        }
        String value = usernameOrEmail.trim();
        return userRepository.findByUsernameIgnoreCase(value)
                .or(() -> userRepository.findByEmailIgnoreCase(value.toLowerCase()))
                .map(user -> {
                    if (user.isDeleted()) {
                        return "deleted";
                    }
                    if (!user.isEnabled()) {
                        return "suspended";
                    }
                    return "error";
                })
                .orElse("error");
    }

    @Transactional
    public String createPasswordResetToken(String username, String email) {
        return userRepository.findByUsernameAndEmail(username.trim(), email.trim().toLowerCase())
                .map(user -> {
                    PasswordResetToken resetToken = new PasswordResetToken();
                    resetToken.setUser(user);
                    resetToken.setToken(java.util.UUID.randomUUID().toString().replace("-", ""));
                    resetToken.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(30));
                    passwordResetTokenRepository.save(resetToken);
                    return resetToken.getToken();
                })
                .orElse(null);
    }

    @Transactional
    public void resetPassword(String token, String password) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Reset link is invalid."));
        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset link has expired. Please request a new one.");
        }
        resetToken.getUser().setPassword(passwordEncoder.encode(password));
        resetToken.setUsed(true);
    }
}
