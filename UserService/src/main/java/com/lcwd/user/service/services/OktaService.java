 package com.lcwd.user.service.services;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.lcwd.user.service.entities.Group;
 import com.lcwd.user.service.entities.Role;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.http.*;
 import org.springframework.stereotype.Service;

 import com.lcwd.user.service.repositories.GroupRepo;
 import com.lcwd.user.service.repositories.RoleRepo;

 import com.okta.sdk.client.Clients;

 import jakarta.annotation.PostConstruct;
 import org.springframework.web.client.RestTemplate;

 import java.io.IOException;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;

 // import com.okta.sdk.client.Client;
 // import com.okta.sdk.client.Clients;
 // import com.okta.sdk.resource.group.Group;
 // import com.okta.sdk.resource.group.GroupList;
 // import com.okta.sdk.resource.group.Role;
 // import com.okta.sdk.resource.group.RoleList;


 @Service
 public class OktaService {

     @Value("00Zfzc-Vfc7FnNTlW4_dT_36-Re7CjD7Ec6RT4Feub")
     private String oktaApiToken;

     @Autowired
     private RoleRepo roleRepo;

     @Autowired
     private GroupRepo groupRepo;


     @PostConstruct
     public void syncOktaData(){

         HttpHeaders headers = new HttpHeaders();
         headers.setBearerAuth(oktaApiToken);
         headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//         Client client = Clients.builder().build();

         //Fetch roles from okta
//         RoleList roles = client.listRoles();
//         for(Role role: roles){
//             roleRepo.save(new Role(role.getProfile().getName()));
//         }

         ResponseEntity<String> rolesResponse = new RestTemplate().exchange(
                 "https://https://dev-68516699-admin.okta.com/api/v1/roles",
                 HttpMethod.GET,
                 new HttpEntity<>(headers),
                 String.class
         );

         //Process rolesResponse and save roles to db

         ObjectMapper objectMapper = new ObjectMapper();
         try {
             //parsing json response to extract roles data
             List<Map<String, Object>> rolesData = objectMapper.readValue(rolesResponse.getBody(), new TypeReference<List<Map<String, Object>>>() {
             });

             //Map roles  data and save to db
             for (Map<String, Object> roleData : rolesData) {
                 String roleName = (String) roleData.get("name");
                 //Create a RoleEntity object and save it using the repository
                 Role roleEntity = new Role(roleName);
                 roleRepo.save(roleEntity);
             }
         }catch (IOException e){
             e.printStackTrace();
         }

         //Fetch groups from okta
//         GroupList groups = client.listGroups();
//         for(Group group: groups){
//             groupRepo.save(new Group(group.getProfile().getName()));
//         }

         ResponseEntity<String> groupResponse = new RestTemplate().exchange(
                 "https://https://dev-68516699-admin.okta.com/api/v1/groups",
                 HttpMethod.GET,
                 new HttpEntity<>(headers),
                 String.class
         );

         //Process GroupResponse and save groups to db
         ObjectMapper objectMapper1 = new ObjectMapper();
         try{
             List<Map<String,Object>> groupsData = objectMapper1.readValue(groupResponse.getBody(), new TypeReference<List<Map<String, Object>>>() {
             });

             //Map groups data and save to db
             for(Map<String,Object> groupData: groupsData){
                 String groupName = (String) groupData.get("name");
                 //Create a Group Entity object and save it using repo
                 Group groupEntity = new Group(groupName);
                 groupRepo.save(groupEntity);
             }
         } catch (IOException e){
             e.printStackTrace();
         }
     }
 }
