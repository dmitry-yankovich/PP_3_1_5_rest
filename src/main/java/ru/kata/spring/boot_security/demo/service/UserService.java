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

    Set <Long> userRolesIdSet(User user);

    //void save(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Set roles);
    void save(User user, Set roles);

    void delete(Long id);

    //void update(Long id, User user, BCryptPasswordEncoder bCryptPasswordEncoder, Set roles);
    void update(User user, Set roles);

    boolean userNameIsVacant(User user);

    boolean roleCollectionIsCorrect(Collection <Role> roleCollection);

    boolean userRolesCollectionIsCorrect(User user);

    boolean additionalCheckIsPassed(User user, Collection <Role> roleCollection);
    public boolean isAdmin(User user);

    public boolean adminIsExistAmongTheUsers(User user, RoleService roleService);

    public boolean adminIsExistAmongTheOtherUsers(User user);
}
