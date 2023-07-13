package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping
    public String index(@RequestParam(value = "count", required = false, defaultValue = "0") int count, Model model) {
        model.addAttribute("users", userService.userList(count));
        return "users";
    }

    @GetMapping("/new")
    public ModelAndView newPerson() {

        User user = new User();
        ModelAndView mav = new ModelAndView("new");
        mav.addObject("user", user);

        List<Role> roles = (List<Role>) roleService.findAll();

        mav.addObject("allRoles", roles);

        List <Long> userRolesId = new ArrayList<Long>();
        mav.addObject("userRolesId", userRolesId);

        return mav;
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id) {

        User user = userService.findById(id);
        List<Role> roles = (List<Role>) roleService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roles);

        List <Long> userRolesId = userService.userRolesId(user);
        model.addAttribute("userRolesId", userRolesId);

        return "edit";
    }

    @PostMapping()
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam(value = "selectedRoles", required = false) Long[] selectedRoles,
                         Model model) {

        List <Role> userRolesToSet = selectedRoles == null ? new ArrayList<Role>() : roleService.findRolesByIds(selectedRoles);

        boolean wrongRoleList = !userService.roleCollectionIsCorrect(userRolesToSet);
        String wrongRoleListMessage = (wrongRoleList) ? "need to choose at least one role!" : "";

        if (bindingResult.hasErrors() || wrongRoleList) {
            List<Role> roles = (List<Role>) roleService.findAll();
            List<Long> userRolesId = selectedRoles == null ? new ArrayList<Long>() : Arrays.asList(selectedRoles);
            model.addAttribute("userRolesId", userRolesId);
            model.addAttribute("allRoles", roles);
            model.addAttribute("wrongRoleList", wrongRoleList);
            model.addAttribute("wrongRoleListMessage", wrongRoleListMessage);
            return "new";
        }

        userService.save(user, bCryptPasswordEncoder, userRolesToSet);
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

        selectedRoles = (selectedRoles==null) ? new Long[] {}: selectedRoles;

        List <Role> userRolesToSet = selectedRoles == null ? new ArrayList<Role>() : roleService.findRolesByIds(selectedRoles);

        boolean wrongRoleList = !userService.roleCollectionIsCorrect(userRolesToSet);
        String wrongRoleListMessage = (wrongRoleList) ? "need to choose at least one role!" : "";

        //List <Role> userRolesToSet = roleService.findRolesByIds(selectedRoles);

        //if (bindingResult.hasErrors() || !userService.additionalCheckIsPassed(user, userRolesToSet)) {
        if (bindingResult.hasErrors() || wrongRoleList) {
            List<Role> roles = (List<Role>) roleService.findAll();
            List<Long> userRolesId = selectedRoles == null ? new ArrayList<Long>() : Arrays.asList(selectedRoles);
            model.addAttribute("userRolesId", userRolesId);
            model.addAttribute("allRoles", roles);
            model.addAttribute("wrongRoleList", wrongRoleList);
            model.addAttribute("wrongRoleListMessage", wrongRoleListMessage);

            //List<Long> userRolesId = Arrays.asList(selectedRoles);

            return "edit";
        }

        userService.update(id, user, bCryptPasswordEncoder, userRolesToSet);
        return "redirect:/admin";
    }
}
