package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RoleService {
    Set<Role> findAll();

    Set<Role> findRolesByIds(Long[] selectedRolesIds);


}
