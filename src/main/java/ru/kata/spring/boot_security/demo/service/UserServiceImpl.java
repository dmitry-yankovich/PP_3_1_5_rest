package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findById (Long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> userList(int number) {

        return userDao.userList(number);
    }

    public Set <Long> userRolesId(User user) {
        return userDao.userRolesId(user);
    }

    @Override
    @Transactional
    public void save(User user, Set roles) {
        userDao.save(user, roles);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userDao.delete(id);
    }

    @Override
    @Transactional
    public void update(Long id, User user, Set roles) {

        userDao.update(id, user, roles);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    @Override
    public boolean userNameIsVacant(User user) {

        String userName = user.getUsername();
        if (userName.isEmpty()) {
            return true;
        }
        User userFindByUserName = userDao.findByUsername(userName);

        if (userFindByUserName.getId() == null) {
            return true;
        }

        return userFindByUserName.getId().equals(user.getId());
    }

    @Override
    public boolean roleCollectionIsCorrect(Collection<Role> roleCollection) {

        return !roleCollection.isEmpty();
    }

    @Override
    public boolean additionalCheckIsPassed(User user, Collection<Role> roleCollection) {
        return userNameIsVacant(user) && roleCollectionIsCorrect(roleCollection);
    }
}