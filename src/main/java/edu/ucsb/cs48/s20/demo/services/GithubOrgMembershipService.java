package edu.ucsb.cs48.s20.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

import com.jcabi.github.Github;
import com.jcabi.github.Organization;
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;
import com.jcabi.github.wire.RetryCarefulWire;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;

import edu.ucsb.cs48.s20.demo.repositories.AdminRepository;

/**
 * Service object that wraps the UCSB Academic Curriculum API
 */
@Service
public class GithubOrgMembershipService implements MembershipService {

    private Logger logger = LoggerFactory.getLogger(GithubOrgMembershipService.class);

    private String githubOrg;

    @Value("${app.admin.logins:}")
    final private List<String> adminLogins = new ArrayList<String>();

    public List<String> getAdminLogins() {
        return adminLogins;
    }

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private AdminRepository adminRepository;

    public GithubOrgMembershipService(@Value("${app.github_org}") String githubOrg) {
        this.githubOrg = githubOrg;
        logger.info("githubOrg=" + githubOrg);
    }

    /**
     * is current logged in user a member but NOT an admin of the github org
     */
    public boolean isMember(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return hasRole(oAuth2AuthenticationToken, "member");
    }

    /** is current logged in user a member of the github org */
    public boolean isAdmin(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return hasRole(oAuth2AuthenticationToken, "admin");
    }

    /**
     * is current logged in user has role
     * 
     * @param roleToTest "member" or "admin"
     * @return if the current logged in user has that role
     */

    public boolean hasRole(OAuth2AuthenticationToken oauthToken, String roleToTest) {
        if (oauthToken == null) {
            return false;
        }
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String user = (String) oAuth2User.getAttributes().get("login");

        Github github = null;

        if (clientService==null) {
            logger.error(String.format("unable to obtain autowired clientService"));
            return false;
        }
        OAuth2AuthorizedClient client = clientService
                .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        if (client==null) {
            logger.info(String.format("clientService was not null but client returned was null for user %s",user));
            return false;
        }

        OAuth2AccessToken token = client.getAccessToken();

        if (token==null) {
            logger.info(String.format("client for %s was not null but getAccessToken returned null",user));
            return false;
        }
        String accessToken = token.getTokenValue();
        if (accessToken==null) {
            logger.info(String.format("token was not null but getTokenValue returned null for user %s",user));
            return false;
        }

        try {
            // I forget why we have Github wrapped like this
            // TODO: find the tutorial that explains it
            // I think it has something to do with respecting rate limits
            github = new RtGithub(new RtGithub(accessToken).entry()
                    .through(RetryCarefulWire.class, 50));

            String path = String.format("/user/memberships/orgs/%s",githubOrg);

            JsonResponse jr = github.entry().uri().path(path).back()
                    .method(Request.GET).fetch().as(JsonResponse.class);

            logger.info("jr =" + jr);

            String actualRole = jr.json().readObject().getString("role");
            String state = jr.json().readObject().getString("state");

            logger.info("actualRole =" + actualRole);
            logger.info("roleToTest =" + roleToTest);
            logger.info("state =" + state);

            // A user is an admin if their username is listed in the "app.admin.logins" property or if their username 
            // is listed in the Admins repository. It does NOT look at GitHub org memberhsip.
            if(roleToTest.equals("admin") && isAdminLogin(user)) {
                return true;
            }

            // A user is a member if they are a member or an admin of the linked GitHub organization.
            if(roleToTest.equals("member") && (actualRole.equals("member") || actualRole.equals("admin"))) {
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error("Exception happened while trying to determine membership in github org");
            logger.error("Exception",e);
        }
        return false;
    }

    public boolean isAdminLogin(String login) {
        return !adminRepository.findByLogin(login).isEmpty() || adminLogins.contains(login);
    }

    public String login(OAuth2AuthenticationToken token) {
        if (token == null)
            return "";
        return token.getPrincipal().getAttributes().get("login").toString();
    }

    public String uid(OAuth2AuthenticationToken token) {
        if (token == null)
            return "";
        return token.getPrincipal().getAttributes().get("id").toString();
    }

}
