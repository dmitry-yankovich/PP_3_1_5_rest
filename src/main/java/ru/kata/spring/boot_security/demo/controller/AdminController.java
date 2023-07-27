package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.servlet.ModelAndView;
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

@Controller
@RequestMapping("admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String index(@RequestParam(value = "count", required = false, defaultValue = "0") int count, Model model) {
        model.addAttribute("users", userService.userList(count));
        return "users";
    }

    @GetMapping("/new")
    public ModelAndView newPerson() {

        ModelAndView mav = new ModelAndView("new");

        User user = new User();
        mav.addObject("user", user);

        Set<Role> roles = roleService.findAll();
        mav.addObject("allRoles", roles);

        Set<Long> userRolesId = new HashSet<Long>();
        mav.addObject("userRolesId", userRolesId);

        return mav;
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id) {

        User user = userService.findById(id);
        model.addAttribute("user", user);

        Set<Role> roles = roleService.findAll();
        model.addAttribute("allRoles", roles);

        Set<Long> userRolesId = userService.userRolesId(user);
        model.addAttribute("userRolesId", userRolesId);

        return "edit";
    }

    @PostMapping()
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam(value = "selectedRoles", required = false) Long[] selectedRoles,
                         Model model) {

        Set<Role> userRolesToSet = selectedRoles == null ? new HashSet<Role>() : roleService.findRolesByIds(selectedRoles);

        boolean wrongRoleList = !userService.roleCollectionIsCorrect(userRolesToSet);

        if (bindingResult.hasErrors() || wrongRoleList) {
            Set<Role> roles = roleService.findAll();
            Set<Long> userRolesId = selectedRoles == null ? new HashSet<Long>() : new HashSet<Long>(Arrays.asList(selectedRoles));
            model.addAttribute("userRolesId", userRolesId);
            model.addAttribute("allRoles", roles);
            model.addAttribute("wrongRoleList", wrongRoleList);
            model.addAttribute("wrongRoleListMessage", (wrongRoleList) ? "need to choose at least one role!" : "");
            return "new";
        }

        userService.save(user, userRolesToSet);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                         @PathVariable("id") Long id, @RequestParam(value = "selectedRoles", required = false) Long[] selectedRoles,
                         @RequestParam(value = "password", required = false) String password,
                         Model model) {

        selectedRoles = (selectedRoles == null) ? new Long[]{} : selectedRoles;

        Set<Role> userRolesToSet = selectedRoles == null ? new HashSet<Role>() : roleService.findRolesByIds(selectedRoles);

        boolean wrongRoleList = !userService.roleCollectionIsCorrect(userRolesToSet);

        if (bindingResult.hasErrors() || wrongRoleList) {
            Set<Role> roles = roleService.findAll();
            Set<Long> userRolesId = selectedRoles == null ? new HashSet<Long>() : new HashSet<Long>(Arrays.asList(selectedRoles));
            model.addAttribute("userRolesId", userRolesId);
            model.addAttribute("allRoles", roles);
            model.addAttribute("wrongRoleList", wrongRoleList);
            model.addAttribute("wrongRoleListMessage", (wrongRoleList) ? "need to choose at least one role!" : "");

            return "edit";
        }

        userService.update(id, user, userRolesToSet);
        return "redirect:/admin";
    }
}
