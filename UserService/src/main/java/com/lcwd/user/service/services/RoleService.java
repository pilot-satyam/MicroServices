package com.lcwd.user.service.services;

import com.lcwd.user.service.entities.Roles;

public interface RoleService {
    Roles findById(int id);
    Roles save(Roles roles);
    Roles createRole(Roles role);
}
