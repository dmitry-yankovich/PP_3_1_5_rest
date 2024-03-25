package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("home")
public class HomeController {

    private final UserService userService;
    private final RoleService roleService;

    public HomeController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String index(@RequestParam(value = "count", required = false, defaultValue = "0") int count, Model model) {

        Set<Role> roles = roleService.findAll();
        model.addAttribute("allRoles", roles);
        model.addAttribute("users", userService.userList(count));
        model.addAttribute("user", new User());
        //model.addAttribute("userWithErrors", null);
        model.addAttribute("authenticatedUser", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        model.addAttribute("userService", userService);
        return "users";
    }
}
