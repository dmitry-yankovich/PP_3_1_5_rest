
package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import ru.kata.spring.boot_security.demo.exception_handling.FieldValidationError;
import ru.kata.spring.boot_security.demo.exception_handling.UserIncorrectException;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class ApiController {

    private final UserService userService;
    private final RoleService roleService;

    private final Validator validator;

    public ApiController(@Lazy UserService userService, @Lazy RoleService roleService, @Lazy Validator validator) {
        this.userService = userService;
        this.roleService = roleService;
        this.validator = validator;
    }

    @Bean
    public Validator validator (final AutowireCapableBeanFactory autowireCapableBeanFactory) {

        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure().constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }

    @GetMapping("/currentUserIsAdmin")
    public Boolean currentUserIsAdmin(){
        return userService.isAdmin(userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @GetMapping("/currentUser")
    public User currentUser(){
        return userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/roles")
    public Set<Role> getAllRoles() {

        Set<Role> roles = roleService.findAll();
        return roles;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {

        List<User> users = userService.userList(0);
        return users;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") Long id) {

        User user = userService.findById(id);
        return user;
    }

    @PutMapping("/users/")
    public User update(@RequestBody User user, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new UserIncorrectException(bindingResult.toString());
        }

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        if (validates.size() > 0) {
            UserIncorrectException ex = new UserIncorrectException("Number of fields validation errors = " + validates.size());
            ex.setErrors(validates.stream().map(x -> new FieldValidationError(x.getPropertyPath().toString(), x.getMessage())).collect(Collectors.toSet()));

            throw ex;
        }

        userService.update(user, roleService.findRolesByIds(user.getRoles().stream().map(x-> x.getId()).toArray(Long[]::new)));

        return userService.findById(user.getId());
    }

    @PostMapping("/users/")
    public User create(@RequestBody User user){

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        if (validates.size() > 0) {
            UserIncorrectException ex = new UserIncorrectException("Number of fields validation errors = " + validates.size());
            ex.setErrors(validates.stream().map(x -> new FieldValidationError(x.getPropertyPath().toString(), x.getMessage())).collect(Collectors.toSet()));

            throw ex;
        }

        userService.save(user, roleService.findRolesByIds(user.getRoles().stream().map(x-> x.getId()).toArray(Long[]::new)));

        return user;
    }

    @DeleteMapping("/users/{id}")
    public DeletingResult delete(@PathVariable("id") Long id) {

        if (!userService.adminIsExistAmongTheOtherUsers(id)) {

            throw new RuntimeException("Must be at least one administrator among the users after deleting");
        }

        //boolean deletingAuthenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId() == id;
        boolean deletingAuthenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId().equals(id);

        userService.delete(id);

        if (deletingAuthenticatedUser) {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }

        return new DeletingResult("User with id = " + id + " has been deleted successfully.", deletingAuthenticatedUser);

        //return "User with id = " + id + " has been deleted successfully.";
    }
}