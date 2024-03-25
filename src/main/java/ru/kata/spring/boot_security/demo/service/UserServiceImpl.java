package ru.kata.spring.boot_security.demo.service;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    @Override
    @Transactional
    public void save(User user, Set roles) {
        userDao.save(user, bCryptPasswordEncoder, roles);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userDao.delete(id);
    }

    @Override
    @Transactional
    public void update(User user, Set roles) {
        userDao.update(user, bCryptPasswordEncoder, roles);
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

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    @Override
    public boolean userNameIsVacant(User user) {

        String userName = user.getUsername();
        if (userName.isEmpty()) {
            return true;
        }
        User userFindByUserName = userDao.findByUsername(userName);

        if (userFindByUserName == null) {
            return true;
        }

        return userFindByUserName.getId().equals(user.getId());
    }

    @Override
    public boolean roleCollectionIsCorrect(Set<Role> roleCollection) {
        if (roleCollection == null) {
            return false;
        }
        return !roleCollection.isEmpty();
    }

    @Override
    public boolean userRolesCollectionIsCorrect(User user) {
        return roleCollectionIsCorrect(user.getRoles());
    }

    @Override
    public boolean isAdmin(User user) {
        return userDao.isAdmin(user);
    }

    @Override
    public boolean adminIsExistAmongTheUsers(User user, RoleService roleService){
            if (user.getRoles() != null && !user.getRoles().stream().filter(x->roleService.findRoleById(x.getId()).getName().equals("ROLE_ADMIN")).collect(Collectors.toList()).isEmpty()) {
            return true;
        }
        return adminIsExistAmongTheOtherUsers(user);
    }

    @Override
    public boolean adminIsExistAmongTheOtherUsers(User user){
        return userDao.adminIsExistAmongTheOtherUsers(user);
    }

    @Override
    public boolean adminIsExistAmongTheOtherUsers(Long id){
        return userDao.adminIsExistAmongTheOtherUsers(id);
    }
}