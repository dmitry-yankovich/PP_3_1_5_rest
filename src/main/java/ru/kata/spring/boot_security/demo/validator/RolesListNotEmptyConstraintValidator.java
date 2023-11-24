package ru.kata.spring.boot_security.demo.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class RolesListNotEmptyConstraintValidator implements ConstraintValidator<RolesListNotEmpty, User> {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(RolesListNotEmpty constraintAnnotation) {

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {

//        if (userService == null) {
//            return true;
//        }

        if (!userService.userRolesCollectionIsCorrect(user)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "{ru.kata.spring.boot_security.demo.validation.RolesListNotEmpty.message}")
                    .addPropertyNode("roles").addConstraintViolation();

            return false;
        }
        return true;
    }
}
