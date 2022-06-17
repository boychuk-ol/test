package com.example.demo.Task;

import com.example.demo.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;


@Controller
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // Головна сторінка сайту. Приймаємо параметр sort з документу для можливого сортування завдань
    @RequestMapping(value = {"/","/tasks", "/home"}, method = RequestMethod.GET)
    public String allTasks(@RequestParam(defaultValue = "taskId") String sort, Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = authentication.getName();

        List<Task> list = taskService.getTasksWithSorting(sort);

        if(userService.findByUsername(s)!=null) {
            list.retainAll(taskService.getAllTasksByUser(userService.findByUsername(s)));
        }
        else
        {
            list.removeIf(user -> user.getUser()!=null);
        }

        model.addAttribute("categories", new HashSet<Task.Category>());
        model.addAttribute("title", "My tasks");
        model.addAttribute("all_tasks", list);
        model.addAttribute("username", s);

        return "mainPage";
    }

    // Додання(створення) нового завдання. Приймаємо параметр title з документу(input) для сетування поля Task.title
    @PostMapping("/tasks/add")
    public String addTask(@RequestParam String title,Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if(title!=null)
        {
            Task task = new Task(title, false, false,userService.findByUsername(username));

            taskService.addTask(task);
            model.addAttribute("added_task", task);
        }
        else
        {
            throw new NullPointerException();
        }

        return "redirect:/tasks";
    }

    // Видалення завдання
    @RequestMapping(value="/tasks/remove/{id}", method = RequestMethod.POST)
    public String removeTask(@PathVariable(value="id") long taskId)
    {
        if (taskId>0) {
            taskService.deleteTask(taskId);
        }
        else
        {
            throw new IllegalArgumentException("Error! This task has wrong id value");
        }
        return "redirect:/tasks";
    }

    // Зміна заголовку завдання. Приймається параметр titleChange з документу(input) для зміни
    @PostMapping(value = "/tasks/changeTitle/{id}")
    public String changeTitle(@PathVariable(value="id") long taskId,@RequestParam("titleChange") String title, Model model)
    {

        if(title!=null && taskId>0 && taskService.getTaskById(taskId)!=null)
        {
            Task task = taskService.getTaskById(taskId);
            task.setTitle(title);
            taskService.addTask(task);
        }
        else
        {
            throw new IllegalArgumentException("Error! This task has wrong id value or title value is null");
        }

        return "redirect:/tasks";
    }

    // Зміна важливості завдання. Для зміни використовується чекбокс, що перемикає значення
    @PostMapping(value = "/tasks/changeImportance/{id}")
    public String changeImportance(@PathVariable(value="id") long taskId)
    {
        if (taskId>0 && taskService.getTaskById(taskId)!=null) {
            Task task = taskService.getTaskById(taskId);
            task.setImportant(!taskService.getTaskById(taskId).isImportant());
            taskService.addTask(task);
        }
        else
        {
            throw new IllegalArgumentException("Error! This task has wrong id value or such a task does not exist");
        }

        return "redirect:/tasks";
    }

    // Зміна статусу виконання завдання. Для зміни використовується чекбокс, що перемикає значення
    @PostMapping(value = "/tasks/changeDone/{id}")
    public String changeDone(@PathVariable(value="id") long taskId)
    {
        if (taskId>0 && taskService.getTaskById(taskId)!=null) {
            Task task = taskService.getTaskById(taskId);
            task.setDone(!taskService.getTaskById(taskId).isDone());
            taskService.addTask(task);
        }
        else
        {
            throw new IllegalArgumentException("Error! This task has wrong id value or such a task does not exist");
        }

        return "redirect:/tasks";
    }

    // Зміна дати завдання. Для зміни використовується параметр localDate з документу(input)
    @PostMapping(value = "/tasks/changeDate/{id}")
    public String changeDate(@PathVariable(value="id") long taskId, @RequestParam String localDate)
    {

        if (taskService.getTaskById(taskId)!=null && taskId>0) {

            Task task = taskService.getTaskById(taskId);
            if (localDate != "" && localDate != null) {
                task.setLocalDate(LocalDate.parse(localDate));
            } else {
                task.setLocalDate(null);
            }
            taskService.addTask(task);
        }
        else
        {
            throw new IllegalArgumentException("Error! This task has wrong id value or such a task does not exist");
        }

        return "redirect:/tasks";
    }

    // Додання/видалення категорій, що асоціюються із завданням. Для зміни використовується параметр categories, що було передано у документ як значення modelAttribute у вигляді об'єкту HashSet<Category>
    @PostMapping(value = "/tasks/changeCategory/{id}")
    public String changeCategory(@PathVariable(value="id") long taskId, @RequestParam(name = "categories",required = false) HashSet<Task.Category> categories)
    {

        if (taskService.getTaskById(taskId)!=null && taskId>0) {
            Task task = taskService.getTaskById(taskId);
            task.setCategory(categories);
            taskService.addTask(task);
        }
        else
        {
            throw new IllegalArgumentException("Error! This task has wrong id value or such a task does not exist");
        }

        return "redirect:/tasks";
    }


    // Отримання лише всіх важливих завдань
    @GetMapping(value = "/important")
    public String important(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = authentication.getName();

        List<Task> list = taskService.getAllOnlyImportantTasks(true);

        if (userService.findByUsername(s)!=null) {
            list.retainAll(taskService.getAllTasksByUser(userService.findByUsername(s)));
        }
        else
        {
            list.removeIf(user -> user.getUser()!=null);
        }

        model.addAttribute("title", "Important");
        model.addAttribute("all_tasks", list);
        model.addAttribute("username", s);

        return "mainPage";
    }

    // Отримання лише всіх виконаних завдань
    @GetMapping(value = "/done")
    public String done(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = authentication.getName();

        List<Task> list = taskService.getAllOnlyDoneTasks(true);

        if (userService.findByUsername(s)!=null) {
            list.retainAll(taskService.getAllTasksByUser(userService.findByUsername(s)));
        }
        else
        {
            list.removeIf(user -> user.getUser()!=null);
        }

        model.addAttribute("title", "Done");
        model.addAttribute("all_tasks", list);
        model.addAttribute("username", s);

        return "mainPage";
    }

    // Отримання лише всіх невиконаних завдань
    @GetMapping(value = "/todo")
    public String todo(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = authentication.getName();

        List<Task> list = taskService.getAllOnlyDoneTasks(false);

        if (userService.findByUsername(s)!=null) {
            list.retainAll(taskService.getAllTasksByUser(userService.findByUsername(s)));
        }
        else
        {
            list.removeIf(user -> user.getUser()!=null);
        }

        model.addAttribute("title", "To do");
        model.addAttribute("all_tasks", list);
        model.addAttribute("username", s);

        return "mainPage";
    }

    // Отримання лише всіх завдань з датою
    @GetMapping(value = "/withDate")
    public String withDate(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = authentication.getName();

        List<Task> list = taskService.getAllTasks();
        list.removeIf(Task -> Task.getLocalDate()==null);

        if (userService.findByUsername(s)!=null) {
            list.retainAll(taskService.getAllTasksByUser(userService.findByUsername(s)));
        }
        else
        {
            list.removeIf(user -> user.getUser()!=null);
        }

        model.addAttribute("title", "With date");
        model.addAttribute("all_tasks", list);
        model.addAttribute("username", s);

        return "mainPage";
    }

    // Отримання лише всіх завдань без дати
    @GetMapping(value = "/withoutDate")
    public String withoutDate(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = authentication.getName();

        List<Task> list = taskService.getAllTasks();
        list.removeIf(Task -> Task.getLocalDate()!=null);

        if (userService.findByUsername(s)!=null) {
            list.retainAll(taskService.getAllTasksByUser(userService.findByUsername(s)));
        }
        else
        {
            list.removeIf(user -> user.getUser()!=null);
        }

        model.addAttribute("title", "Without date");
        model.addAttribute("all_tasks", list);
        model.addAttribute("username", s);

        return "mainPage";
    }
}
