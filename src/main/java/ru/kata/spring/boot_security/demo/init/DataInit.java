package ru.kata.spring.boot_security.demo.init;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DataInit {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInit(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    void postConstruct() {
        List<User> users = userService.userList(0);

        if (users.isEmpty()) {
            Role admin = new Role("ROLE_ADMIN");
            Role user = new Role("ROLE_USER");

            userService.save(new User("admin",
                    "administrator",
                    "admin@gmail.com",
                    "super"), new HashSet<>(Arrays.asList(admin)));
            userService.save(new User("user",
                    "mister_user",
                    "user@gmail.com",
                    "user"), new HashSet<>(Arrays.asList(user)));
        }
    }
}
