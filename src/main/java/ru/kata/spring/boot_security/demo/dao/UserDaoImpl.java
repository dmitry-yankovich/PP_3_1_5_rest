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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDaoImpl implements UserDao{

    @PersistenceContext
    private EntityManager entityManager;

    public UserDaoImpl() {}

    public User findByUsername(String username) {

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email=:name", User.class);

        query.setParameter("name", username);

        List<User> userList = query.getResultList();

        return userList.isEmpty() ? null : userList.get(0);

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

    public Set<Long> userRolesIdSet(User user) {

        //Query query = entityManager.createQuery("SELECT role.id FROM User user JOIN user.roles role WHERE user = :user");
        TypedQuery<Long> query = entityManager.createQuery("SELECT role.id FROM User user JOIN user.roles role WHERE user = :user", Long.class);
        query.setParameter("user", user);

        return new HashSet<Long>(query.getResultList());
    }

    @Override
    public void save(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Set roles) {

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
    public void update(User user, BCryptPasswordEncoder bCryptPasswordEncoder, Set roles) {

        User userToBeUpdated = entityManager.find(User.class, user.getId());

        userToBeUpdated.setName(user.getName());
        userToBeUpdated.setLastName(user.getLastName());
        userToBeUpdated.setEmail(user.getEmail());
        userToBeUpdated.setAge(user.getAge());
        if (!userToBeUpdated.getPassword().equals(user.getPassword())) {
            userToBeUpdated.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userToBeUpdated.setRoles(roles);
    }

    @Override
    public boolean isAdmin(User user) {
        //Query query = entityManager.createQuery("SELECT role.id FROM User user JOIN user.roles role WHERE user = :user AND role.name = 'ROLE_ADMIN'");
        TypedQuery<Long> query = entityManager.createQuery("SELECT role.id FROM User user JOIN user.roles role WHERE user = :user AND role.name = :roleAdminName", Long.class);
        query.setParameter("user", user);
        query.setParameter("roleAdminName", "ROLE_ADMIN");

        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean adminIsExistAmongTheOtherUsers(User user){
        TypedQuery<Role> query = entityManager.createQuery("SELECT role FROM User user JOIN user.roles role WHERE role.name = :roleAdminName AND NOT user.id = :userId", Role.class);
        query.setParameter("roleAdminName", "ROLE_ADMIN");
        query.setParameter("userId", user.getId());

        return !query.getResultList().isEmpty();
    }

    @Override
    public List<Role> userRoles(User user){
        TypedQuery<Role> query = entityManager.createQuery("SELECT role FROM User user JOIN user.roles role WHERE user.id = :userId", Role.class);
        query.setParameter("userId", user.getId());

        return query.getResultList();
    }
}
