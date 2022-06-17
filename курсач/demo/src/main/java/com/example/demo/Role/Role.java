package com.example.demo.Role;
import com.example.demo.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Set;


// Клас для надання ролі юзеру
@Entity
@Table(name="role_s")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @NotNull
    private String name;

    @Autowired
    public Role(String name) {
        this.name = name;
    }

    @Autowired
    public Role() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // Юзер може мати декілька ролей. Роль може належати декілька юзерам
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users;

}
