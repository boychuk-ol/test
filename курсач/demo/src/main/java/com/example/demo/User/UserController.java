package com.example.demo.User;

import com.example.demo.Task.TaskService;
import com.example.demo.User.Security.SecurityService;
import com.example.demo.User.Validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class UserController {

    private final UserService userService;
    private final TaskService taskService;
    private final UserValidator userValidator;
    private final SecurityService securityService;

    @Autowired
    public UserController(UserService userService, TaskService taskService, UserValidator userValidator, SecurityService securityService) {
        this.userService = userService;
        this.taskService = taskService;
        this.userValidator = userValidator;
        this.securityService = securityService;
    }

    public String getUsernameUsingSecurityContext()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {

        if(error != null)
        {
            model.addAttribute("error","Username or password is incorrect");
        }
        if (logout !=null)
        {
            model.addAttribute("message", "Logged out successfully");
        }

        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    // Передача браузеру страницы с формой
    @RequestMapping(value = {"/registration"}, method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    // Обработка данных формы
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult result) {

        // Валидация с помощью валидатора
        userValidator.validate(userForm, result);

        // Если есть ошибки - показ формы с сообщениями об ошибках
        if (result.hasErrors()) {
            return "registration";
        }

        // Сохранение пользователя в базе
        userService.save(userForm);

        securityService.manualLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        // Перенаправление на страницу
        return "redirect:/tasks";
    }

    @GetMapping("/deleteUser")
    public String deleteUser() {

        if (userService.findByUsername(getUsernameUsingSecurityContext())!=null) {
            taskService.deleteAllTasksByUser(userService.findByUsername(getUsernameUsingSecurityContext()));
            userService.remove(userService.findByUsername(getUsernameUsingSecurityContext()));
        }
        else throw new NullPointerException();

        return "redirect:/logout";
    }

    @RequestMapping(value = {"/myprofile"}, method = RequestMethod.GET)
    public String myProfile(Model model) {
        model.addAttribute("userName",getUsernameUsingSecurityContext());
        return "myProfile";
    }

}
