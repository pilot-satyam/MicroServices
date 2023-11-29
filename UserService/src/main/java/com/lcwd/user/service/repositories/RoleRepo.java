package com.lcwd.user.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.user.service.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{   
}
