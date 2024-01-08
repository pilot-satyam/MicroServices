package com.lcwd.gateway.controllers;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.gateway.models.AuthResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/sso")
    public ResponseEntity<AuthResponse> login(
        @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
        @AuthenticationPrincipal OidcUser user,
        Model model
    ){
         logger.info("user's email: {}",user.getEmail());
         AuthResponse authResponse = new AuthResponse();
        //  setting email to auth response
         authResponse.setUserId(user.getEmail());

        //setting token to auth response
         authResponse.setAccessToken(client.getAccessToken().getTokenValue());

         authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());

         authResponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());

         List<String> authorities = user.getAuthorities().stream().map(grantedAuthority -> {
            return grantedAuthority.getAuthority();
         }).collect(Collectors.toList());
         authResponse.setAuthorities(authorities);


        return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
    }
    
}
