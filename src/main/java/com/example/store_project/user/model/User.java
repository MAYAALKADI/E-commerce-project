package com.example.store_project.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role; 

    // الحقول الجديدة الخاصة بحظر المحاولات الفاشلة
    @Column(name = "failed_attempt")
    private int failedAttempt = 0;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    public User() {}

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.failedAttempt = 0;
        this.accountNonLocked = true;
    }

    // أضيفي هذا الحقل مع الحقول السابقة
private java.time.LocalDateTime lockTime;

// أضيفي هذه الدوال في أسفل الملف
public java.time.LocalDateTime getLockTime() { return lockTime; }
public void setLockTime(java.time.LocalDateTime lockTime) { this.lockTime = lockTime; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Getters and Setters للحقول الجديدة
    public int getFailedAttempt() { return failedAttempt; }
    public void setFailedAttempt(int failedAttempt) { this.failedAttempt = failedAttempt; }

    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
}