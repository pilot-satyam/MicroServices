package com.lcwd.user.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.user.service.entities.Roles;

public interface RoleRepo extends JpaRepository<Roles, Integer> {
    
}
