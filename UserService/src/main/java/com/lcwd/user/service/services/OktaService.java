package com.lcwd.user.service.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.lcwd.user.service.entities.GroupUser;
import com.lcwd.user.service.entities.Role;
import com.lcwd.user.service.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.lcwd.user.service.repositories.GroupUserRepo;
import com.lcwd.user.service.repositories.RoleRepo;
import com.lcwd.user.service.repositories.UserRepository;

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

     @Autowired
     private UserRepository userRepo;

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

      //CRUD Operations
        
        String apiUrl = "https://dev-68516699-admin.okta.com/api/v1/users?activate=false";
        HttpHeaders headers3 = new HttpHeaders();
        headers3.set("Authorization", "SSWS " + oktaApiKey);
        headers3.setContentType(MediaType.APPLICATION_JSON);
        String groupId = "00gdgw8j7fc1gahSJ5d7";
        String requestBody = "{ \"profile\": { \"firstName\": \"Isaac\", \"lastName\": \"Brock\", \"email\": \"isaac17@gmail.com\", \"login\": \"isaac17@gmail.com\" }, \"groupIds\": [\"" + groupId + "\"]}";
        System.out.println("Request Body: " + requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers3);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // Parse the response body to get user details
                ObjectMapper objectMapper3 = new ObjectMapper();
                JsonNode jsonNode = objectMapper3.readTree(response.getBody());

                String oktaId = jsonNode.get("id").asText();
                String firstName = jsonNode.get("profile").get("firstName").asText();
                // String lastName = jsonNode.get("profile").get("lastName").asText();
                String email = jsonNode.get("profile").get("email").asText();

                // Create a User instance and save it to the database
                User newUser = new User();
                newUser.setUserId(oktaId);
                newUser.setName(firstName);
                // newUser.setLastName(lastName);
                newUser.setEmail(email);
                userRepo.save(newUser);
                System.out.println("User created and saved successfully!");
            } 
            catch (Exception e) {
                System.out.println("Error processing response: " + e.getMessage());
            }
        } else {
            System.out.println("Failed to create user. Status code: " + response.getStatusCodeValue());
        }
    }
 }
