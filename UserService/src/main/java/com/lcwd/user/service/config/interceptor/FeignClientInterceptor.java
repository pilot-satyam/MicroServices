package com.lcwd.user.service.config.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
@Configuration
public class FeignClientInterceptor implements RequestInterceptor{

    @Autowired
    private OAuth2AuthorizedClientManager manager;

    @Override
    public void apply(RequestTemplate arg0) {
    
      String token = manager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("internal-client").principal("internal").build()).getAccessToken().getTokenValue(); 
       arg0.header("Authorization","Bearer "+token);
    }
    
}
