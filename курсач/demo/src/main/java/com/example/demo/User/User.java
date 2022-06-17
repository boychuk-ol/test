package com.example.demo.User;

import com.example.demo.Role.Role;
import com.example.demo.Task.Task;
import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

// Клас юзерів(користувачів)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private long userId;

    private String username,password;

    @Transient
    private String passwordConfirm;

    // Юзер може мати декілька ролей. Роль може належати декілька юзерам
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Юзер може мати безліч завдань. Будь-яке з завдань належить лише одному юзеру
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private Collection<Task> tasks;

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
