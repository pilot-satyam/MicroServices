// package com.lcwd.user.service.services;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.client.RestTemplate;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.lcwd.user.service.entities.User;
// import com.lcwd.user.service.repositories.UserRepository;

// @Service
// public class OktaCreate {
//     @Autowired
//     private UserRepository userRepository;

//     private String apiUrl = "https://dev-68516699-admin.okta.com/api/v1/users?activate=false";

//     @Value("00Zfzc-Vfc7FnNTlW4_dT_36-Re7CjD7Ec6RT4Feub")
//     private String oktaApiKey;

//     // @PostMapping("/create")
//     public ResponseEntity<String> createUser(User user) {
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "SSWS " + oktaApiKey);
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         String requestBody = "{ \"profile\": { \"firstName\": \"" + user.getName() + "\", \"lastName\": \"" + user.getName() + "\", \"email\": \"" + user.getEmail() + "\", \"login\": \"" + user.getEmail() + "\" }, \"groupIds\": \"00gdgw8j7fc1gahSJ5d7\"}";

//         HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//         RestTemplate restTemplate = new RestTemplate();

//         ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

//         if (response.getStatusCode() == HttpStatus.CREATED) {
//             try {
//                 // Parse the response body to get user details
//                 ObjectMapper objectMapper = new ObjectMapper();
//                 JsonNode jsonNode = objectMapper.readTree(response.getBody());

//                 String oktaId = jsonNode.get("id").asText();
//                 String firstName = jsonNode.get("profile").get("firstName").asText();
//                 String lastName = jsonNode.get("profile").get("lastName").asText();
//                 String email = jsonNode.get("profile").get("email").asText();

//                 // Create a User instance and save it to the database
//                 User newUser = new User();
//                 user.setUserId(oktaId);
//                 user.setName(firstName);
//                 // user.setLastName(lastName);
//                 user.setEmail(email);

//                 userRepository.save(newUser);

//                 return ResponseEntity.ok("User created and saved successfully!");
//             } catch (Exception e) {
//                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing response: " + e.getMessage());
//             }
//         } else {
//             return ResponseEntity.status(response.getStatusCode()).body("Failed to create user. Status code: " + response.getStatusCodeValue());
//         }
//     }
// }
