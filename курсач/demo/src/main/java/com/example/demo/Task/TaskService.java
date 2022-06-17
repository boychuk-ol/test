package com.example.demo.Task;

import com.example.demo.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    @Autowired
    public TaskService(TaskRepository repository)
    {
        this.repository = repository;
    }

    public List<Task> getAllTasks()
    {
        return repository.findAll();
    }

    public void deleteAllTasksByUser(User user) {repository.deleteAll(this.getAllTasksByUser(user));}

    public List<Task> getAllOnlyImportantTasks(boolean isImportant){return repository.findAllByIsImportant(isImportant);}

    public List<Task> getAllOnlyDoneTasks(boolean isDone){return repository.findAllByIsDone(isDone);}

    public List<Task> getAllTasksByUser(User user) {return repository.findAllByUser(user);}

    public List<Task> getTasksWithSorting(String sortValue){return repository.findAll(Sort.by(Sort.Direction.ASC,sortValue));}

    public Task getTaskById(long taskId){return repository.getOne(taskId);}

    public void addTask(Task task)
    {
        repository.saveAndFlush(task);
    }

    public void deleteTask(long taskId){
        repository.deleteById(taskId);
    }

}