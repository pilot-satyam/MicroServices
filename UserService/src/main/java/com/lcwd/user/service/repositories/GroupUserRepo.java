package com.lcwd.user.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lcwd.user.service.entities.GroupUser;

@Repository
public interface GroupUserRepo extends JpaRepository<GroupUser, String> {
    
}
