package com.lcwd.user.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import com.lcwd.user.service.entities.GroupsOkta;
import com.lcwd.user.service.entities.Roles;
import com.lcwd.user.service.repositories.GroupRepo;
import com.lcwd.user.service.repositories.RoleRepo;
import com.lcwd.user.service.services.GroupService;
import com.lcwd.user.service.services.RoleService;

@RestController
@RequestMapping("/groups")
public class GroupController {
    
    @Autowired
    private GroupService groupService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private RoleRepo roleRepo;

    //Append role to group
    @PostMapping("/{groupId}/roles/{roleId}")
    public ResponseEntity<?> assginRoleToGroup
    (@PathVariable int groupId,
     @PathVariable int roleId,
     @RequestBody Map<String, String> requestBody
     ){
        String roleName = requestBody.get("roleName");
        GroupsOkta group = groupService.findById(groupId);
        Roles role = roleService.findById(roleId);
        if(group !=null && role!=null){
            group.setRoleName(roleName);
            group = groupService.save(group);
            String roleNameSaved = group.getRoleName();
            if (roleNameSaved != null) {
                return ResponseEntity.ok("Role assigned to group successfully. Role name: " + roleNameSaved);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Role name might not have been saved properly.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group or Role not found");
        }
    }

    //create group
    @PostMapping("/create")
    public ResponseEntity<GroupsOkta> createGroup(@RequestBody GroupsOkta group){
        GroupsOkta group1 = groupService.save(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(group1);
    }

    // delete a group
    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<?> deleteGroup
    (@PathVariable Integer groupId){
        GroupsOkta group = groupRepo.findById(groupId).orElse(null);
        if(group != null){
            groupRepo.deleteById(groupId);
            return ResponseEntity.ok("Group Deleted Successfully!!!");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group Not Found!!!");
        }
    }

    //delete a role
    @DeleteMapping("/roles/delete/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Integer roleId){
        Roles role = roleRepo.findById(roleId).orElse(null);
        if(role !=null){
            roleRepo.deleteById(roleId);
            return ResponseEntity.ok("Role Deleted Successfully!!!");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Roles Not Found!");
        }
    }

    //create Role
    @PostMapping("/role/create")
    public ResponseEntity<Roles> createRole(@RequestBody Roles roles){
        Roles role1 = roleService.save(roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(role1);
    }

    //Update Group
    @PutMapping("/update/{groupId}")
    public ResponseEntity<GroupsOkta> updateGroup(@RequestBody GroupsOkta updatedGroup,@PathVariable Integer groupId){
        GroupsOkta group = groupRepo.findById(groupId).orElse(null);
        if(group != null){
            group.setGroupName(updatedGroup.getGroupName());
            group.setPath(updatedGroup.getPath());
            group.setRoleName(updatedGroup.getRoleName());
            group.setType(updatedGroup.getType());
    
            GroupsOkta updated = groupRepo.save(group);
            return ResponseEntity.ok(updated);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    //update Roles in DB
    @PutMapping("/roles/update/{roleId}")
    public ResponseEntity<Roles> updateRoles(@PathVariable Integer roleId,@RequestBody Roles updatedRoles){
        Roles role = roleRepo.findById(roleId).orElse(null);
        if(role!=null){
            role.setRoleName(updatedRoles.getRoleName());
            role.setRoleDesc(updatedRoles.getRoleDesc());
            Roles updatedRole = roleRepo.save(role);
            return ResponseEntity.ok(updatedRole);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}

