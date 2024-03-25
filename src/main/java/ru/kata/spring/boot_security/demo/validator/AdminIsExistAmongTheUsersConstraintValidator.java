package ru.kata.spring.boot_security.demo.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class AdminIsExistAmongTheUsersConstraintValidator implements ConstraintValidator<AdminIsExistAmongTheUsers, User> {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public void setUserService(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void initialize(AdminIsExistAmongTheUsers constraintAnnotation) {

        ConstraintValidator.super.initialize(constraintAnnotation);
        org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {

//        if (userService == null) {
//            return true;
//        }

        if (user.getId() != null && !userService.adminIsExistAmongTheUsers(user, roleService)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "{ru.kata.spring.boot_security.demo.validation.AdminIsExistAmongTheUsers.message}")
                    .addPropertyNode("roles").addConstraintViolation();

            return false;
        }
        return true;
    }
}
