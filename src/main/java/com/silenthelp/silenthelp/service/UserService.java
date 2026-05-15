package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.dto.RegistrationForm;
import com.silenthelp.silenthelp.model.AccountStatus;
import com.silenthelp.silenthelp.model.EmailVerificationToken;
import com.silenthelp.silenthelp.model.Role;
import com.silenthelp.silenthelp.model.PasswordResetToken;
import com.silenthelp.silenthelp.model.RoleName;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.EmailVerificationTokenRepository;
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

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final String allowedEmailDomains;
    private final String currentPolicyVersion;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.registration.allowed-email-domains:edu,edu.in,ac.in}") String allowedEmailDomains,
                       @Value("${app.policy.version:2026-05}") String currentPolicyVersion) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.allowedEmailDomains = allowedEmailDomains;
        this.currentPolicyVersion = currentPolicyVersion;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!canAuthenticate(user))
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                        .toList())
                .build();
    }

    @Transactional
    public String registerStudent(RegistrationForm form) {
        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();
        if (!isAllowedCollegeEmail(email)) {
            throw new IllegalArgumentException("Only college email addresses are allowed. Use your official .edu, .edu.in, or .ac.in email.");
        }

        Optional<User> existingUserOpt = userRepository.findByEmailIgnoreCase(email);
        if (existingUserOpt.isPresent()) {
            throw new IllegalArgumentException("Email is already registered. If this account was deactivated, request reactivation instead.");
        }

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
        user.setEnrollmentNumber(form.getEnrollmentNumber().trim());
        user.setVerifiedStudent(false);
        user.setEmailVerified(false);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEnabled(false);
        user.setSuspicious(isSuspiciousRegistration(form));
        if (user.isSuspicious()) {
            user.setReviewNote("Registration needs review: weak enrollment number or generic profile details.");
        }
        user.getRoles().add(role);
        userRepository.save(user);
        return createEmailVerificationToken(user);
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
        user.setEnrollmentNumber(form.getEnrollmentNumber() == null ? null : form.getEnrollmentNumber().trim());
        user.setVerifiedStudent(true);
        user.setVerifiedByAdmin(true);
        user.setVerifiedAt(LocalDateTime.now());
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setTermsAccepted(true);
        user.setTermsAcceptedAt(LocalDateTime.now());
        user.setPolicyVersion(currentPolicyVersion);
        user.setAccountStatus(AccountStatus.ACTIVE);
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
        if (user.getAccountStatus() == AccountStatus.DELETED) {
            return;
        }
        user.setAccountStatus(enabled ? AccountStatus.ACTIVE : AccountStatus.BANNED);
    }

    @Transactional
    public void internallyVerify(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setVerified(true);
        user.setVerifiedByAdmin(true);
        user.setVerifiedAt(LocalDateTime.now());
        user.setSuspicious(false);
        user.setReviewNote(null);
    }

    @Transactional
    public User deactivateOwnAccount(String username, String reason) {
        if (reason == null || reason.trim().length() < 10) {
            throw new IllegalArgumentException("Please share a reason with at least 10 characters.");
        }
        User user = requireByUsername(username);
        if (user.hasRole(RoleName.ADMIN)) {
            throw new IllegalArgumentException("Admin accounts cannot be deactivated from this page.");
        }
        user.setAccountStatus(AccountStatus.DEACTIVATED);
        user.setDeletionReason(reason.trim());
        user.setDeletedAt(LocalDateTime.now());
        return user;
    }

    @Transactional
    public User approveReactivation(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setDeletionReason(null);
        user.setDeletedAt(null);
        user.setReactivatedAt(LocalDateTime.now());
        return user;
    }

    @Transactional
    public User rejectReactivation(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setAccountStatus(AccountStatus.DEACTIVATED);
        return user;
    }

    @Transactional
    public User requestReactivation(String usernameOrEmail, String reason) {
        if (reason == null || reason.trim().length() < 10) {
            throw new IllegalArgumentException("Please share a reason with at least 10 characters.");
        }
        User user = userRepository.findByUsernameIgnoreCase(usernameOrEmail.trim())
                .or(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail.trim().toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("No deactivated account was found for those details."));
        if (user.getAccountStatus() != AccountStatus.DEACTIVATED && !user.isDeleted()) {
            throw new IllegalArgumentException("This account is not currently deactivated.");
        }
        user.setAccountStatus(AccountStatus.PENDING_REACTIVATION);
        user.setDeletionReason(reason.trim());
        return user;
    }

    @Transactional
    public User removePersonalInformation(String username, String reason) {
        if (reason == null || reason.trim().length() < 10) {
            throw new IllegalArgumentException("Please share a reason with at least 10 characters.");
        }
        User user = requireByUsername(username);
        if (user.hasRole(RoleName.ADMIN)) {
            throw new IllegalArgumentException("Admin profile information cannot be removed from this page.");
        }
        Long id = user.getId();
        user.setDisplayName("Deleted User");
        user.setUsername("deleted_user_" + id);
        user.setEmail("removed+" + id + "@silenthelp.local");
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setDepartment(null);
        user.setYearOfStudy(null);
        user.setEnrollmentNumber(null);
        user.setVerifiedStudent(false);
        user.setVerifiedByAdmin(false);
        user.setVerifiedAt(null);
        user.setAccountStatus(AccountStatus.DELETED);
        user.setDeletionReason(reason.trim());
        user.setDeletedAt(LocalDateTime.now());
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
        return userRepository.findDistinctByRolesNameAndAccountStatusIn(RoleName.STUDENT,
                List.of(AccountStatus.DEACTIVATED, AccountStatus.PENDING_REACTIVATION), pageable);
    }

    @Transactional(readOnly = true)
    public long deletedStudentCount() {
        return userRepository.countDistinctByRolesNameAndAccountStatusIn(RoleName.STUDENT,
                List.of(AccountStatus.DEACTIVATED, AccountStatus.PENDING_REACTIVATION));
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
                    if (!user.isEmailVerified()) {
                        return "unverified";
                    }
                    if (user.getAccountStatus() == AccountStatus.DEACTIVATED) {
                        return "deactivated";
                    }
                    if (user.getAccountStatus() == AccountStatus.PENDING_REACTIVATION) {
                        return "pending_reactivation";
                    }
                    if (user.getAccountStatus() == AccountStatus.BANNED) {
                        return "banned";
                    }
                    if (user.getAccountStatus() == AccountStatus.DELETED) {
                        return "removed";
                    }
                    return "error";
                })
                .orElse("error");
    }

    @Transactional
    public User verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Verification link is invalid."));
        if (verificationToken.isUsed() || verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification link has expired. Please register again or contact support.");
        }
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setEnabled(true);
        verificationToken.setUsed(true);
        return user;
    }

    @Transactional
    public String createEmailVerificationTokenForAccount(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new IllegalArgumentException("Enter your username or college email.");
        }
        String value = usernameOrEmail.trim();
        User user = userRepository.findByUsernameIgnoreCase(value)
                .or(() -> userRepository.findByEmailIgnoreCase(value.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("No account was found for those details."));
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("This email is already verified. Please sign in.");
        }
        if (user.getAccountStatus() == AccountStatus.DEACTIVATED || user.getAccountStatus() == AccountStatus.PENDING_REACTIVATION) {
            throw new IllegalArgumentException("This account is deactivated. Please request reactivation instead.");
        }
        if (user.getAccountStatus() == AccountStatus.BANNED || user.getAccountStatus() == AccountStatus.DELETED) {
            throw new IllegalArgumentException("This account cannot be verified from this page. Contact admin support.");
        }
        return createEmailVerificationToken(user);
    }

    @Transactional(readOnly = true)
    public boolean needsPolicyAcceptance(String username) {
        User user = requireByUsername(username);
        return !user.isTermsAccepted() || !currentPolicyVersion.equals(user.getPolicyVersion());
    }

    @Transactional
    public void acceptCurrentPolicies(String username) {
        User user = requireByUsername(username);
        user.setTermsAccepted(true);
        user.setTermsAcceptedAt(LocalDateTime.now());
        user.setPolicyVersion(currentPolicyVersion);
    }

    public String currentPolicyVersion() {
        return currentPolicyVersion;
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

    private boolean canAuthenticate(User user) {
        return user.isEmailVerified() && user.getAccountStatus() == AccountStatus.ACTIVE && user.isEnabled();
    }

    private String createEmailVerificationToken(User user) {
        emailVerificationTokenRepository.deleteByUser(user);
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailVerificationTokenRepository.save(token);
        return token.getToken();
    }

    private boolean isSuspiciousRegistration(RegistrationForm form) {
        String enrollment = form.getEnrollmentNumber() == null ? "" : form.getEnrollmentNumber().trim();
        String display = form.getDisplayName() == null ? "" : form.getDisplayName().trim().toLowerCase();
        return enrollment.length() < 5 || display.contains("test") || display.contains("fake");
    }
}
