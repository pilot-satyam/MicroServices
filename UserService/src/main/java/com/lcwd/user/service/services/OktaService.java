 package com.lcwd.user.service.services;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.ObjectMapper;

import com.lcwd.user.service.entities.GroupUser;
import com.lcwd.user.service.entities.Role;

import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.http.*;
 import org.springframework.stereotype.Service;

 import com.lcwd.user.service.repositories.GroupUserRepo;
import com.lcwd.user.service.repositories.RoleRepo;

import jakarta.annotation.PostConstruct;
 import org.springframework.web.client.RestTemplate;

 import java.io.IOException;
 import java.util.List;
 import java.util.Map;

 @Service
 public class OktaService {

     @Value("00AebMAfXer8ZxaeXLpRRIoi0XZjbekwu6Ub-sJDY8")
     private String oktaApiKey;

     @Autowired
     private GroupUserRepo groupRepo;
    
     @Autowired
     private RoleRepo roleRepo;

     @PostConstruct
     public void syncOktaData() {

         String groupsEndpoint = "https://dev-68516699-admin.okta.com/api/v1/groups";

         HttpHeaders headers = new HttpHeaders();
         headers.set("Authorization", "SSWS " + oktaApiKey);
         headers.setContentType(MediaType.APPLICATION_JSON);

         HttpEntity<String> entity = new HttpEntity<>(headers);

         ResponseEntity<String> groupResponse = new RestTemplate().exchange(
                 groupsEndpoint,
                 HttpMethod.GET,
                 entity,
                 String.class
         );

         ObjectMapper objectMapper = new ObjectMapper();
         try {
             List<Map<String, Object>> groupsData = objectMapper.readValue(groupResponse.getBody(),
                     new TypeReference<List<Map<String, Object>>>() {});

             for (Map<String, Object> groupData : groupsData) {
                 String groupId = (String) groupData.get("id");
                 String groupName = null;
                 Object profileObj = groupData.get("profile");
                 if (profileObj instanceof Map) {
                     Map<String, Object> profileMap = (Map<String, Object>) profileObj;
                     groupName = (String) profileMap.get("name");
                 }

                 if (groupId != null && groupName != null) {
                     GroupUser groupEntity = new GroupUser();
                     groupEntity.setId(groupId);
                     groupEntity.setName(groupName);

                     groupRepo.save(groupEntity);
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }

         String userId = "cr0dggawbrNSze5DO5d7";
         String rolesEndpoint = "https://dev-68516699-admin.okta.com/api/v1/iam/roles/" + userId;

         HttpHeaders headers2 = new HttpHeaders();
         headers2.set("Authorization", "SSWS " + oktaApiKey);
         headers2.setContentType(MediaType.APPLICATION_JSON);
 
         HttpEntity<String> entity2 = new HttpEntity<>(headers);
 
         ResponseEntity<String> roleResponse = new RestTemplate().exchange(
                 rolesEndpoint,
                 HttpMethod.GET,
                 entity2,
                 String.class
         );
 
         ObjectMapper objectMapper2 = new ObjectMapper();
         try {
            Map<String, Object> roleData = objectMapper2.readValue(roleResponse.getBody(),
            new TypeReference<Map<String, Object>>() {});
 
            String roleId = (String) roleData.get("id");
            String roleLabel = (String) roleData.get("label");
        
            if (roleId != null && roleLabel != null) {
                Role role = new Role(roleId, roleLabel);
                roleRepo.save(role);
            }
        } catch (IOException e) {
            e.printStackTrace();
      }
     }
 }
