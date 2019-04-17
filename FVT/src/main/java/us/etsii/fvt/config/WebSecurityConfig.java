package us.etsii.fvt.config;

import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class takes care of some configuration elements of the
 *         application related with the security.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Private attribute in charge of encoding passwords.
     */
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Attribute that helps to manages the login access.
     */
    @Autowired
    CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;

    /**
     * Method that manages the access to the data.
     * 
     * @return a service that can be used to access to the data of users.
     */
    @Bean
    public UserDetailsService mongoUserDetails() {
	return new UserService();
    }

    /**
     * Method that configures the encode and the user service.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	UserDetailsService userDetailsService = mongoUserDetails();
	auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

    }

    /**
     * Method that manages the permissions to access to some resources of the
     * application.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
	http.authorizeRequests().antMatchers(CommonsResources.SLASH).permitAll()
		.antMatchers(CommonsResources.SLASH + PathResources.LOG_IN).permitAll()
		.antMatchers(CommonsResources.SLASH + PathResources.SIGN_UP).permitAll()
		.antMatchers(
			CommonsResources.SLASH + PathResources.DASHBOARD + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK,
			CommonsResources.SLASH + PathResources.TRACKING + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK,
			CommonsResources.SLASH + PathResources.VULNERABILITY + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK,
			CommonsResources.SLASH + PathResources.SETTING + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK,
			CommonsResources.SLASH + PathResources.NOTIFICATION + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK,
			CommonsResources.SLASH + PathResources.SAVE_SETTING + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK,
			CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH
				+ CommonsResources.DOUBLE_ASTERISK)
		.authenticated().and().csrf().disable().formLogin()
		.successHandler(customizeAuthenticationSuccessHandler)
		.loginPage(CommonsResources.SLASH + PathResources.LOG_IN)
		.failureUrl(CommonsResources.SLASH + PathResources.LOG_IN + CommonsResources.QUESTION
			+ PathResources.ERROR + CommonsResources.EQUAL + PathResources.TRUE)
		.usernameParameter(Parameters.EMAIL).passwordParameter(Parameters.PASSWORD).and().logout()
		.logoutRequestMatcher(new AntPathRequestMatcher(CommonsResources.SLASH + PathResources.LOG_OUT))
		.logoutSuccessUrl(CommonsResources.SLASH).and().exceptionHandling();
    }

    /**
     * Method that manages the load of some resources in the server in order to be
     * accessible in the deployment.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
	web.ignoring().antMatchers(
		CommonsResources.SLASH + PathResources.RESOURCES + CommonsResources.SLASH
			+ CommonsResources.DOUBLE_ASTERISK,
		CommonsResources.SLASH + PathResources.STATIC + CommonsResources.SLASH
			+ CommonsResources.DOUBLE_ASTERISK,
		CommonsResources.SLASH + PathResources.CSS + CommonsResources.SLASH + CommonsResources.DOUBLE_ASTERISK,
		CommonsResources.SLASH + PathResources.JS + CommonsResources.SLASH + CommonsResources.DOUBLE_ASTERISK,
		CommonsResources.SLASH + PathResources.IMAGES + CommonsResources.SLASH
			+ CommonsResources.DOUBLE_ASTERISK);
    }
}
