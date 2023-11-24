package ru.kata.spring.boot_security.demo.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RolesListNotEmptyConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RolesListNotEmpty {
    String message() default "{ru.kata.spring.boot_security.demo.validation.RolesList.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};

}
