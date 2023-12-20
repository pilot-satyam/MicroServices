package com.lcwd.gateway.controllers;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.gateway.models.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AuthController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
        @AuthenticationPrincipal OidcUser user,
        Model model
    ){
        String email = user.getEmail();
        OAuth2AccessToken accessToken = client.getAccessToken();
        String refreshToken = client.getRefreshToken().getTokenValue();
        Long expireAt = client.getAccessToken().getExpiresAt().getEpochSecond();

        // Check if the access token exists in Redis
        String cachedToken = redisTemplate.opsForValue().get(email);

        if (cachedToken != null) {
            // If token exists in Redis cache, return the cached token along with authorities
            List<String> authorities = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            AuthResponse authResponse = createAuthResponse(cachedToken, authorities,refreshToken,expireAt,email);
            return ResponseEntity.ok(authResponse);
        } else {
            // Store the access token and authorities in Redis
            String tokenValue = accessToken.getTokenValue();
            List<String> authorities = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            redisTemplate.opsForValue().set(email, tokenValue);
            redisTemplate.expire(email, 60, TimeUnit.SECONDS);
            // Cache authorities separately (example: key = email_authorities)
            redisTemplate.opsForValue().set(email + "_authorities", authorities.toString());
            redisTemplate.expire(email + "_authorities", 60, TimeUnit.SECONDS);

            // Return the token and authorities as a response
            AuthResponse authResponse = createAuthResponse(tokenValue, authorities,refreshToken,expireAt,email);
            return ResponseEntity.ok(authResponse);
        }
    }

    private AuthResponse createAuthResponse(String accessToken, List<String> authorities,String refreshToken,Long expireAt,String email) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(email);
        authResponse.setAccessToken(accessToken);
        authResponse.setAuthorities(authorities);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setExpireAt(expireAt);
        return authResponse;
    }
}
