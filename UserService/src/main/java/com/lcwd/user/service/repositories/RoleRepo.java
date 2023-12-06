package com.lcwd.user.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lcwd.user.service.entities.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, String>{   
}
