package com.lcwd.user.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.user.service.entities.Group;

public interface GroupRepo extends JpaRepository<Group, Long> {

    
} 