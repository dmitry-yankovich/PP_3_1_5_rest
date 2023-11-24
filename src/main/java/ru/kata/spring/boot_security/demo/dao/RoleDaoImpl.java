package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class RoleDaoImpl implements RoleDao{
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Set<Role> findAll() {
        //TypedQuery<Role> query = entityManager.createQuery("select r from Role r", Role.class);
        TypedQuery<Role> query = entityManager.createQuery("from Role", Role.class);
        return new HashSet <Role>(query.getResultList());
    }

    @Override
    public Set<Role> findRolesByIds(Long[] selectedRolesIds) {
        TypedQuery<Role> query = entityManager.createQuery("select r from Role r where r.id in (:selectedRolesIds)", Role.class);
        query.setParameter("selectedRolesIds", Arrays.asList(selectedRolesIds));
        return new HashSet<Role>(query.getResultList());
    }

    @Override
    public Role findRoleById(Long id) {
        TypedQuery<Role> query = entityManager.createQuery("select r from Role r where r.id = :roleId", Role.class);
        query.setParameter("roleId", id);

        List<Role> roleList = query.getResultList();

        return roleList.isEmpty() ? null : roleList.get(0);

    }
}
