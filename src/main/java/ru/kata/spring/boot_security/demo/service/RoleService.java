package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;
import java.util.List;

public interface RoleService {
    List<Role> findAll();

    List<Role> findRolesByIds(Long[] selectedRolesIds);


}
