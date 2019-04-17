package us.etsii.fvt.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import us.etsii.fvt.domains.User;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the login controller.
 */
@Controller
public class LoginController {

    /**
     * User service attribute.
     */
    @Autowired
    private UserService userService;

    /**
     * Notification service attribute.
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);

    /**
     * Method that maps the request for the resource '/login' through of a get HTTP
     * method.
     * 
     * @return a new view with the login form.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.LOG_IN, method = RequestMethod.GET)
    public ModelAndView login() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
		    new Object[] { PathResources.LOG_IN, null }));
	    ModelAndView modelAndView = new ModelAndView();
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = null;
	    // Si tenemos acceso a la información de autenticación, intentamos recuperar el
	    // usuario.
	    if (auth != null) {
		user = userService.findUserByEmail(auth.getName());
	    }
	    // Añadimos el usuario al modelo.
	    modelAndView.addObject(Parameters.CURRENT_USER, user);
	    if (user != null) {
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);
	    }
	    modelAndView.setViewName(PathResources.LOG_IN);
	    return modelAndView;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/singup' through of a get HTTP
     * method.
     * 
     * @return a new view with the sign up form.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.SIGN_UP, method = RequestMethod.GET)
    public ModelAndView signup() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
		    new Object[] { PathResources.SIGN_UP, null }));
	    ModelAndView modelAndView = new ModelAndView();

	    // Si el usuario ya esta registrado, lo incluimos en el modelo.
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    // Si tenemos acceso a la información de autenticación, intentamos recuperar el
	    // usuario.
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
		    modelAndView.addObject(Parameters.CURRENT_USER, user);
		    modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());
		}
	    }
	    User user = new User();
	    modelAndView.addObject(Parameters.USER, user);
	    modelAndView.setViewName(PathResources.SIGN_UP);
	    return modelAndView;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/signup' through of a post
     * HTTP method and save the form in the database.
     * 
     * @param user   User object.
     * @param errors Binding object to return errors to form.
     * @return a new view with the login form.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.SIGN_UP, method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult errors) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
		    new Object[] { PathResources.SIGN_UP, user.getId() }));

	    // Comprobamos que el usuario no este ya registrado.
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    // Si tenemos acceso a la información de autenticación, intentamos recuperar el
	    // usuario.
	    if (auth != null) {
		User userExists = userService.findUserByEmail(auth.getName());
		// Si el usuario es distinto de nulo, no realizamos ninguna operación y nos
		// dirigimos a la ventana de home.
		if (userExists != null) {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ACCESS_SIGN_UP, true,
			    new Object[] { userExists.getId() }));
		    return new ModelAndView(
			    Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.HOME);
		}
	    }
	    ModelAndView modelAndView = new ModelAndView();
	    User userExists = userService.findUserByEmail(user.getEmail());
	    if (userExists != null) {
		errors.rejectValue(Parameters.EMAIL, PathResources.ERROR + CommonsResources.DOT + Parameters.USER,
			I18n.getResource(LanguageKeys.ERROR_USER_ALREADY_EXISTS, false));
		LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_USER_ALREADY_EXISTS, true,
			new Object[] { user.getEmail() }));
	    }
	    if (errors.hasErrors()) {
		LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SIGNUP_ERROR_REGISTERED, true));
		modelAndView.addObject(Parameters.ERRORS, errors.getAllErrors());
		modelAndView.setViewName(PathResources.SIGN_UP);
	    } else {
		userService.saveUser(user);
		LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SIGNUP_SUCCESS, true,
			new Object[] { user.getId() }));
		modelAndView.addObject(Parameters.SUCCESS_MESSAGE,
			I18n.getResource(LanguageKeys.USER_CREATED_SUCCESSFULLY, false));
		modelAndView.addObject(Parameters.USER, new User());
		modelAndView.setViewName(PathResources.LOG_IN);

	    }
	    return modelAndView;
	} catch (

	Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

}
