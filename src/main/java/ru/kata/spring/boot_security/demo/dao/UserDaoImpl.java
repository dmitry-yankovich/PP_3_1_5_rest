package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao{

    @PersistenceContext
    private EntityManager em;

    public User findByUsername(String username) {

        TypedQuery<User> query = em.createQuery("select u from User u where u.name=:name", User.class);

        query.setParameter("name", username);

        List<User> userList = query.getResultList();

        return userList.isEmpty() ? new User() : userList.get(0);

    }

    public User findById (Long id) {

        User user = em.find(User.class, id);

        return user;
    }

    public List<User> index() {
        return userList();
    }

    public List<User> userList() {

        return userList(0);
    }

    public List<User> userList(int number) {

        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        if (number != 0) {
            query.setMaxResults(number);
        }
        return query.getResultList();
    }

    public List<Long> userRolesId(User user) {

        Query query = em.createQuery("SELECT role.id FROM User user JOIN user.roles role WHERE user = :user");
        query.setParameter("user", user);

        return query.getResultList();
    }

    @Override
    public void save(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Collection roles) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        user.setRoles(roles);
        em.persist(user);
    }

    @Override
    public void delete(Long id) {

        User user = em.find(User.class, id);
        em.remove(user);
    }

    @Override
    public void update(Long id, User user, BCryptPasswordEncoder bCryptPasswordEncoder, Collection roles) {

        User userToBeUpdated = em.find(User.class, id);

        userToBeUpdated.setName(user.getName());
        userToBeUpdated.setLastName(user.getLastName());
        userToBeUpdated.setEmail(user.getEmail());
        if (!userToBeUpdated.getPassword().equals(user.getPassword())) {
            userToBeUpdated.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userToBeUpdated.setRoles(roles);
    }
}
