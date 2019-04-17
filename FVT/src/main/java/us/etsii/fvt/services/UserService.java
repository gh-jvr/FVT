package us.etsii.fvt.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.etsii.fvt.domains.Role;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.repositories.RoleRepository;
import us.etsii.fvt.repositories.UserRepository;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Roles;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the user service.
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    /**
     * User repository attribute.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Role repository attribute.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Password encoder attribute.
     */
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    /**
     * Method that finds an user by its email.
     * 
     * @param email String representation of the email to match.
     * @return the user whose email matches with the given one.
     */
    public User findUserByEmail(String email) {
	try {
	    User user = userRepository.findByEmail(email);
	    if (user != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_USER_FOUND, true,
			new Object[] { user.getId() }));
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_USER_NOT_FOUND, true));
	    }
	    return user;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that saves an user in the database.
     * 
     * @param user User object to save.
     */
    public void saveUser(User user) {
	try {
	    if (user != null) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setEnabled(true);
		Role userRole = roleRepository.findByRole(Roles.USER);
		user.setRoles(new HashSet<>(Arrays.asList(userRole)));
		userRepository.save(user);
		LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_USER_SAVED_SUCCESSFULLY, true,
			new Object[] { user.getId(), user.getEmail(), Roles.USER }));
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_NULL, true));
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that load the authority of an user by its email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	try {
	    User user = userRepository.findByEmail(email);
	    if (user != null) {
		List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
		return buildUserForAuthentication(user, authorities);
	    } else {
		throw new UsernameNotFoundException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Auxiliary method that gets the list of granted authority for a set of roles.
     * 
     * @param userRoles Set of user roles.
     * @return a list of granted authority for the given user roles.
     */
    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
	try {
	    Set<GrantedAuthority> roles = new HashSet<>();
	    userRoles.forEach((role) -> {
		roles.add(new SimpleGrantedAuthority(role.getRole()));
	    });

	    List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
	    return grantedAuthorities;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
	return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
