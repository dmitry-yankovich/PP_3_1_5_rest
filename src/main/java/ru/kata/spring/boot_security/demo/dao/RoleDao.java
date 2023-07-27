package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;
import java.util.Set;

//@Repository
public interface RoleDao {

    Set<Role> findAll();

    Set<Role> findRolesByIds(Long[] selectedRolesIds);
}
