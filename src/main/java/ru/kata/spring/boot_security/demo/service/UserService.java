package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {

    User findByUsername(String username);
    User findById (Long id);
    List<User> userList(int number);

    List <Long> userRolesId(User user);

    void save(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Collection roles);

    void delete(Long id);

    void update(Long id, User user, BCryptPasswordEncoder bCryptPasswordEncoder, Collection roles);

    boolean userNameIsVacant(User user);

    boolean roleCollectionIsCorrect(Collection <Role> roleCollection);

    boolean additionalCheckIsPassed(User user, Collection <Role> roleCollection);
}
