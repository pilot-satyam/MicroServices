package com.lcwd.user.service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcwd.user.service.controllers.OktaController.RequestBodyStructure;
// import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.repositories.UserRepository;

import lombok.Data;

@RestController
@RequestMapping("/users")
public class OktaController {

    @Autowired
    private UserRepository userRepository;

    private final String apiUrl = "https://dev-68516699-admin.okta.com/api/v1/users?activate=false";
    private final String apiToken = "00Zfzc-Vfc7FnNTlW4_dT_36-Re7CjD7Ec6RT4Feub";

    //CREATE Request
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody RequestBodyStructure requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SSWS " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String requestBodyString = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> request = new HttpEntity<>(requestBodyString, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String oktaId = jsonNode.get("id").asText();
                String savedFirstName = jsonNode.get("profile").get("firstName").asText();
                String savedLastName = jsonNode.get("profile").get("lastName").asText();
                String savedEmail = jsonNode.get("profile").get("email").asText();

                User user = new User();
                user.setUserId(oktaId);
                user.setName(savedFirstName);
                // user.setLastName(savedLastName);
                user.setEmail(savedEmail);

                userRepository.save(user);

                return ResponseEntity.ok("User created and saved successfully!");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Failed to create user. Status code: " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    //PUT Request 
    String apiUrl2 = "https://dev-68516699-admin.okta.com/api/v1/users";
    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(
        @PathVariable String userId,
        @RequestBody RequestPutBodyStructure requestBody
    )
    {
        String updateUserUrl = apiUrl2 + "/" + userId;
        HttpHeaders headers4 = new HttpHeaders();
        headers4.set("Authorization", "SSWS " + apiToken);
        headers4.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestBodyString = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> request = new HttpEntity<>(requestBodyString, headers4);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                updateUserUrl,
                HttpMethod.PUT,
                request,
                String.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {

                User updatedUser = userRepository.findById(userId).orElse(null);
                if(updatedUser != null){
                    updatedUser.setName(requestBody.getProfile().getFirstName());
                    updatedUser.setEmail(requestBody.getProfile().getEmail());
                    userRepository.save(updatedUser);
                }
                return ResponseEntity.ok("User updated successfully!");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Failed to update user. Status code: " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

     //DELETE Request
        String deleteUrl = "https://dev-68516699-admin.okta.com/api/v1/users";
        @DeleteMapping("/delete/{userId}")
        public ResponseEntity<String> deleteUser(@PathVariable String userId){
            String deleteUserUrl = deleteUrl + "/"+userId;
            HttpHeaders headers5 = new HttpHeaders();
            headers5.set("Authorization","SSWS "+apiToken);
            headers5.setContentType(MediaType.APPLICATION_JSON);
            try{
                //deleting user from okta
                HttpEntity<String> requestEntity = new HttpEntity<>(null,headers5);
                RestTemplate restTemplate2 = new RestTemplate();
                ResponseEntity<String> oktaResponse = restTemplate2.exchange(
                    deleteUserUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
                );
                if(oktaResponse.getStatusCode() == HttpStatus.NO_CONTENT){
                    //delete from local db
                    userRepository.deleteById(userId);
                    return ResponseEntity.ok("User deleted from Okta and local DB!!!");
                }
                else{
                    return ResponseEntity.status(oktaResponse.getStatusCode()).body("Failed to delete from Okta. Status Code: "+oktaResponse.getStatusCodeValue());
                }
            }
            catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Deleting User: "+e.getMessage());
            }
        }

    @Data
    static class RequestBodyStructure {
        private Profile profile;
        private List<String> groupIds;
    }

    @Data
    static class RequestPutBodyStructure {
        private Profile profile;
    }

    @Data
    static class Profile {
        private String firstName;
        private String lastName;
        private String email;
        private String login;
    }
}