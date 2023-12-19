package com.lcwd.user.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.repositories.UserRepository;

@RestController
@RequestMapping("/users")
public class OktaController {

    @Autowired
    private UserRepository userRepository;

    private final String apiUrl = "https://dev-68516699-admin.okta.com/api/v1/users?activate=false";
    private final String apiToken = "00Zfzc-Vfc7FnNTlW4_dT_36-Re7CjD7Ec6RT4Feub";

    @PostMapping("/create")
    public ResponseEntity<String> createUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SSWS " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{ \"profile\": { \"firstName\": \"Isaac\", \"lastName\": \"Brock\", \"email\": \"isaac@gmail.com\", \"login\": \"isaac@gmail.com\" }, \"groupIds\": \"00gdgw8j7fc1gahSJ5d7\"}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            try {
                // Parse the response body to get user details
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                String oktaId = jsonNode.get("id").asText();
                String firstName = jsonNode.get("profile").get("firstName").asText();
                String lastName = jsonNode.get("profile").get("lastName").asText();
                String email = jsonNode.get("profile").get("email").asText();

                // Create a User instance and save it to the database
                User user = new User();
                user.setUserId(oktaId);
                user.setName(firstName);
                // user.setLastName(lastName);
                user.setEmail(email);

                userRepository.save(user);

                return ResponseEntity.ok("User created and saved successfully!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing response: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to create user. Status code: " + response.getStatusCodeValue());
        }
    }
}
