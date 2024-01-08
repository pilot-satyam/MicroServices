package com.lcwd.user.service.services;

import com.lcwd.user.service.entities.GroupsOkta;

public interface GroupService {
    GroupsOkta findById(int id);
    GroupsOkta save(GroupsOkta group);
    GroupsOkta createUser(GroupsOkta user);
}
