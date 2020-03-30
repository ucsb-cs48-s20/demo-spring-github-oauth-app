package edu.ucsb.cs48.s20.demo.advice;

import org.springframework.web.bind.annotation.ModelAttribute;

import edu.ucsb.cs48.s20.demo.controllers.AppUsersController;
import edu.ucsb.cs48.s20.demo.entities.AppUser;
import edu.ucsb.cs48.s20.demo.repositories.AppUserRepository;
import edu.ucsb.cs48.s20.demo.services.MembershipService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class AuthControllerAdvice {

    @Autowired   
    private MembershipService membershipService;

    @Autowired   
    private AppUserRepository appUserRepository;

    @ModelAttribute("isLoggedIn")
    public boolean getIsLoggedIn(OAuth2AuthenticationToken token){
        updateLoginTable(token);
        return token != null;
    }

    @ModelAttribute("id")
    public String getUid(OAuth2AuthenticationToken token){
        if (token == null) return "";
        return token.getPrincipal().getAttributes().get("id").toString();
    }

    @ModelAttribute("login")
    public String getLogin(OAuth2AuthenticationToken token){
        if (token == null) return "";
        return token.getPrincipal().getAttributes().get("login").toString();
    }

  
    @ModelAttribute("isMember")
    public boolean getIsMember(OAuth2AuthenticationToken token){
        return membershipService.isMember(token);
    }

    @ModelAttribute("isAdmin")
    public boolean getIsAdmin(OAuth2AuthenticationToken token){
        return membershipService.isAdmin(token);
    }

    @ModelAttribute("role")
    public String getRole(OAuth2AuthenticationToken token){
        return membershipService.role(token);
    }

    private void updateLoginTable(OAuth2AuthenticationToken token) {
        if (token==null) return;
        
        String login = membershipService.login(token);
        if (login == null) return;

        List<AppUser> appUsers = appUserRepository.findByLogin(login);

        if (appUsers.size()==0) {
            // No user with this email is in the AppUsers table yet, so add one
            AppUser u = new AppUser();
            u.setLogin(login);
            u.setUid(membershipService.uid(token));
            appUserRepository.save(u);
        }
    }
}