package us.etsii.fvt.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.PathResources;

/**
 * 
 * @author Javier Villalba Ramírez Free Vulnerabilities Tracker, 2019
 * 
 *         This class takes care of managing the HTTP authentication petitions.
 */
@Component
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * This method sends back a 200 HTTP status code and redirects the user to the
     * dashboard view.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	    Authentication authentication) throws IOException, ServletException {
	response.setStatus(HttpServletResponse.SC_OK);
	response.sendRedirect(CommonsResources.SLASH + PathResources.DASHBOARD);
    }
}
