package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
public class RoleDaoImpl implements RoleDao{
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<Role> findAll() {
        TypedQuery<Role> query = em.createQuery("select r from Role r", Role.class);
        return query.getResultList();
    }

    @Override
    public List<Role> findRolesByIds(Long[] selectedRolesIds) {
        TypedQuery<Role> query = em.createQuery("select r from Role r where r.id in (:selectedRolesIds)", Role.class);
        query.setParameter("selectedRolesIds", Arrays.asList(selectedRolesIds));
        return query.getResultList();
    }
}
