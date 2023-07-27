package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class UserDaoImpl implements UserDao{

    @PersistenceContext
    private EntityManager entityManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDaoImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findByUsername(String username) {

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.name=:name", User.class);

        query.setParameter("name", username);

        List<User> userList = query.getResultList();

        return userList.isEmpty() ? new User() : userList.get(0);

    }

    public User findById (Long id) {

        User user = entityManager.find(User.class, id);

        return user;
    }

    public List<User> index() {
        return userList();
    }

    public List<User> userList() {

        return userList(0);
    }

    public List<User> userList(int number) {

        //TypedQuery<User> query = entityManager.createQuery("select u from User u", User.class);
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        if (number != 0) {
            query.setMaxResults(number);
        }
        return query.getResultList();
    }

    public Set<Long> userRolesId(User user) {

        Query query = entityManager.createQuery("SELECT role.id FROM User user JOIN user.roles role WHERE user = :user");
        query.setParameter("user", user);

        return new HashSet<Long>(query.getResultList());
    }

    @Override
    @Transactional
    //public void save(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Set roles) {
    public void save(User user, Set roles) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        user.setRoles(roles);
        entityManager.persist(user);
    }

    @Override
    public void delete(Long id) {

        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Override
    //public void update(Long id, User user, BCryptPasswordEncoder bCryptPasswordEncoder, Set roles) {
    public void update(Long id, User user, Set roles) {

        User userToBeUpdated = entityManager.find(User.class, id);

        userToBeUpdated.setName(user.getName());
        userToBeUpdated.setLastName(user.getLastName());
        userToBeUpdated.setEmail(user.getEmail());
        if (!userToBeUpdated.getPassword().equals(user.getPassword())) {
            userToBeUpdated.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userToBeUpdated.setRoles(roles);
    }
}
