package com.example.webbanhang.controller;

import com.example.webbanhang.model.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/SSO")
public class Controller {
    @GetMapping("/signingoogle")
    public Map<String, Object> currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        System.out.println(toPerson(oAuth2AuthenticationToken.getPrincipal().getAttributes()).getEmail());
        System.out.println(toPerson(oAuth2AuthenticationToken.getPrincipal().getAttributes()).getEmail());
        System.out.println(toPerson(oAuth2AuthenticationToken.getPrincipal().getAttributes()).getEmail());
        return oAuth2AuthenticationToken.getPrincipal().getAttributes();
    }
    public User toPerson(Map<String, Object> map) {
        if(map==null) {return null;}
        User user = new User();
        user.setEmail((String) map.get("email"));
        return user;
    }
}
