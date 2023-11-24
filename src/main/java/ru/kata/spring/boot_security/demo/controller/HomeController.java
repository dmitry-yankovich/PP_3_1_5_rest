
package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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

    @PostMapping()
    public String create(@ModelAttribute("user") @Valid User newUser,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            model.addAttribute("authenticatedUser", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
            model.addAttribute("userService", userService);
            model.addAttribute("selectedRolesIdSet", newUser.getRoles() == null ? new HashSet<Role>() : newUser.getRoles().stream().map(x-> Long.parseLong(x.getName())).collect(Collectors.toSet()));
            model.addAttribute("continueEditNewUser", true);
            model.addAttribute("users", userService.userList(0));

            return "users";
        }

        userService.save(newUser, roleService.findRolesByIds(newUser.getRoles().stream().map(x-> Long.parseLong(x.getName())).toArray(Long[]::new)));
        return "redirect:/home";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id, @ModelAttribute("user") User userToDelete, Model model) {

        if (!userService.adminIsExistAmongTheOtherUsers(userToDelete)) {

            model.addAttribute("allRoles", roleService.findAll());
            model.addAttribute("users", userService.userList(0));
            model.addAttribute("authenticatedUser", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
            model.addAttribute("userService", userService);
            model.addAttribute("userCanNotBeDeleted", userToDelete);
//            model.addAttribute("selectedRolesIdSet", userToDelete.getRoles() == null ? new HashSet<Role>() : userToDelete.getRoles().stream().map(x-> Long.parseLong(x.getName())).collect(Collectors.toSet()));
            model.addAttribute("selectedRolesIdSet", userService.userRolesIdSet(userToDelete));
            model.addAttribute("messageToShowDeleteDisable", "Must be at least one administrator among the users after deleting");

            return "users";
        }
        boolean deletingAuthenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId() == id;
        userService.delete(id);
        if (deletingAuthenticatedUser) {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }

        return "redirect:/home";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User updatedUser, BindingResult bindingResult, Model model) {

        //model.addAttribute("authenticatedUser", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));

        if (bindingResult.hasErrors()) {

            model.addAttribute("allRoles", roleService.findAll());
            model.addAttribute("users", userService.userList(0));
            model.addAttribute("authenticatedUser", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
            model.addAttribute("userService", userService);
            model.addAttribute("userWithErrors", updatedUser);
            model.addAttribute("selectedRolesIdSet", updatedUser.getRoles() == null ? new HashSet<Role>() : updatedUser.getRoles().stream().map(x-> Long.parseLong(x.getName())).collect(Collectors.toSet()));

            return "users";
        }

        boolean updatingAuthenticatedUserName = (userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId() == updatedUser.getId()) && (!SecurityContextHolder.getContext().getAuthentication().getName().equals(updatedUser.getEmail()));

        userService.update(updatedUser, roleService.findRolesByIds(updatedUser.getRoles().stream().map(x-> Long.parseLong(x.getName())).toArray(Long[]::new)));

        if (updatingAuthenticatedUserName) {
            Authentication oldAuthenticationObject = SecurityContextHolder.getContext().getAuthentication();

            SecurityContextHolder.clearContext();
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            updatedUser.getEmail(),//NEWUSERNAME
                            null,
                            oldAuthenticationObject.getAuthorities());
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        model.addAttribute("authenticatedUser", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));

        return "redirect:/home";
    }

}