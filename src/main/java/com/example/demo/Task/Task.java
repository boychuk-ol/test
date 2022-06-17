package com.example.demo.Task;

import com.example.demo.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

// Клас для збереження завдань
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long taskId;

    private String title;

    // Завдання може мати деякі категорії, що відрізняються за кольором
    @ElementCollection
    private Set<Category> category;

    public enum Category {
        Blue, Red, Green, Yellow, Orange, Violet;
    }

    private boolean isImportant,isDone;
    private LocalDate localDate;

    // Юзер може мати багато завдань. Кожне завдання має відношення лише до одного юзера
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

    @Autowired
    public Task(String title, boolean isImportant, boolean isDone) {
        this.title = title;
        this.category = new HashSet<>();
        this.isImportant = isImportant;
        this.isDone = isDone;
    }

    @Autowired
    public Task(String title, boolean isImportant, boolean isDone, User user) {
        this.title = title;
        this.category = new HashSet<>();
        this.user = user;
        this.isImportant = isImportant;
        this.isDone = isDone;
    }

    @Autowired
    public Task() {}

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Category> getCategory() {return category;}

    public void setCategory(Set<Category> category) {this.category = category;}

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        this.isImportant = important;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLocalDate() {return localDate;}

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}