package com.lcwd.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lcwd.user.service.entities.GroupsOkta;
import com.lcwd.user.service.repositories.GroupRepo;
import com.lcwd.user.service.services.GroupService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepo groupRepo;

    @Override
    public GroupsOkta findById(int id) {
        GroupsOkta group = groupRepo.findById(id).orElse(null);
        if(group == null){
            throw new EntityNotFoundException("Group not found for Id:" + id);
        }
        return group;
    }

    @Override
    public GroupsOkta save(GroupsOkta group) {
        return groupRepo.save(group);
    }

    @Override
    public GroupsOkta createUser(GroupsOkta user) {
        return groupRepo.save(user);
    }
    
}
