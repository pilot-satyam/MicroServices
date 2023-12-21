package com.lcwd.user.service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Data
    static class RequestBodyStructure {
        private Profile profile;
        private List<String> groupIds;
    }

    @Data
    static class Profile {
        private String firstName;
        private String lastName;
        private String email;
        private String login;
    }
}