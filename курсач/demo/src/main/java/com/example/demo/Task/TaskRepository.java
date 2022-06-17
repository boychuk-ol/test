package com.example.demo.Task;

import com.example.demo.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);
    List<Task> findAllByIsImportant(boolean isImportant);
    List<Task> findAllByIsDone(boolean isDone);
}
