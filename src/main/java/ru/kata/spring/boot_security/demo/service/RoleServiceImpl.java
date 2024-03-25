package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Set<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public Set<Role> findRolesByIds(Long[] selectedRolesIds) {
        return roleDao.findRolesByIds(selectedRolesIds);
    }

    @Override
    public Role findRoleById(Long id) {
        return roleDao.findRoleById(id);
    }

}
