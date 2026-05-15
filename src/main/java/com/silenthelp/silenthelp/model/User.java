package com.silenthelp.silenthelp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_username", columnList = "username", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, unique = true, length = 140)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 120)
    private String displayName;

    @Column(length = 120)
    private String department;

    @Column(length = 40)
    private String yearOfStudy;

    @Column(length = 60)
    private String enrollmentNumber;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verifiedStudent = false;

    @Column(name = "is_verified", nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verifiedByAdmin = false;

    private LocalDateTime verifiedAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean emailVerified = false;

    private LocalDateTime emailVerifiedAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean termsAccepted = false;

    private LocalDateTime termsAcceptedAt;

    @Column(length = 40)
    private String policyVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40, columnDefinition = "varchar(40) default 'ACTIVE'")
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean suspicious = false;

    @Column(length = 500)
    private String reviewNote;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(length = 500)
    private String deletionReason;

    private LocalDateTime deletedAt;

    private LocalDateTime reactivatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public boolean isVerifiedStudent() {
        return verifiedStudent || verified;
    }

    public void setVerifiedStudent(boolean verifiedStudent) {
        this.verifiedStudent = verifiedStudent;
        this.verified = verifiedStudent;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
        this.verifiedStudent = verified;
    }

    public boolean isVerifiedByAdmin() {
        return verifiedByAdmin;
    }

    public void setVerifiedByAdmin(boolean verifiedByAdmin) {
        this.verifiedByAdmin = verifiedByAdmin;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public LocalDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public void setTermsAcceptedAt(LocalDateTime termsAcceptedAt) {
        this.termsAcceptedAt = termsAcceptedAt;
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(String policyVersion) {
        this.policyVersion = policyVersion;
    }

    public AccountStatus getAccountStatus() {
        if (accountStatus == null) {
            if (deleted && !enabled) {
                return AccountStatus.DELETED;
            }
            if (deleted) {
                return AccountStatus.DEACTIVATED;
            }
            if (!enabled) {
                return AccountStatus.BANNED;
            }
            return AccountStatus.ACTIVE;
        }
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        this.enabled = accountStatus == AccountStatus.ACTIVE;
        this.deleted = accountStatus == AccountStatus.DEACTIVATED
                || accountStatus == AccountStatus.PENDING_REACTIVATION
                || accountStatus == AccountStatus.DELETED;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletionReason() {
        return deletionReason;
    }

    public void setDeletionReason(String deletionReason) {
        this.deletionReason = deletionReason;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getReactivatedAt() {
        return reactivatedAt;
    }

    public void setReactivatedAt(LocalDateTime reactivatedAt) {
        this.reactivatedAt = reactivatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean hasRole(RoleName roleName) {
        return roles.stream().anyMatch(role -> role.getName() == roleName);
    }

    public String getTrustLevel() {
        if (hasRole(RoleName.ADMIN)) {
            return "Admin";
        }
        if (verifiedByAdmin || verified) {
            return "Internally Verified";
        }
        if (emailVerified && termsAccepted) {
            return "Trusted User";
        }
        return "New User";
    }
}
