package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserDao {

    User findByUsername(String username);
    User findById (Long id);
    List<User> index();
    List<User> userList(int number);

    List<Long> userRolesId(User user);

    void save(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Collection roles);

    void delete(Long id);

    void update(Long id, User user, BCryptPasswordEncoder bCryptPasswordEncoder, Collection roles);
}
