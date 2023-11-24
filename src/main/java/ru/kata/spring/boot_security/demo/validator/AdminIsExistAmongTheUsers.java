package ru.kata.spring.boot_security.demo.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AdminIsExistAmongTheUsersConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminIsExistAmongTheUsers {
    String message() default "{ru.kata.spring.boot_security.demo.validation.AdminIsExistAmongTheUsers.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};

}
