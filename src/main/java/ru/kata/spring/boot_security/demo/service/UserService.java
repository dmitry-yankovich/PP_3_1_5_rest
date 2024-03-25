package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

    User findByUsername(String username);
    User findById (Long id);
    List<User> userList(int number);

    void save(User user, Set roles);

    void delete(Long id);

    void update(User user, Set roles);

    boolean userNameIsVacant(User user);

    boolean roleCollectionIsCorrect(Set <Role> roleCollection);

    boolean userRolesCollectionIsCorrect(User user);

    boolean isAdmin(User user);

    boolean adminIsExistAmongTheUsers(User user, RoleService roleService);

    boolean adminIsExistAmongTheOtherUsers(User user);

    boolean adminIsExistAmongTheOtherUsers(Long id);
}
