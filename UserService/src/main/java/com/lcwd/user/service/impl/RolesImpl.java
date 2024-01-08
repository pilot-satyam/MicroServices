package com.lcwd.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lcwd.user.service.entities.Roles;
import com.lcwd.user.service.repositories.RoleRepo;
import com.lcwd.user.service.services.RoleService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RolesImpl implements RoleService{

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public Roles findById(int id) {
        Roles role = roleRepo.findById(id).orElse(null);
        if(role == null){
            throw new EntityNotFoundException("Role does not exist: "+role);
        }
        return role;
    }

    @Override
    public Roles save(Roles roles) {
        return roleRepo.save(roles);
    }

    @Override
    public Roles createRole(Roles role) {
      return roleRepo.save(role);
    }
    
}
