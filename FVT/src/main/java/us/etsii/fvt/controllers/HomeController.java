package us.etsii.fvt.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the home controller.
 */
@Controller
public class HomeController {

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
    private static final Logger LOGGER = LogManager.getLogger(HomeController.class);

    /**
     * Method that maps the request for the resource '/home' or '/' through of a get
     * HTTP method.
     * 
     * @return a new view with the home page.
     */
    @RequestMapping(value = { CommonsResources.SLASH,
	    CommonsResources.SLASH + PathResources.HOME }, method = RequestMethod.GET)
    public ModelAndView home() {
	try {
	    // inicializamos la variable que albergará el modelo e intentamos recuperar el
	    // usuario registrado.
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
	    // Si existe un usuario registrado, añadimos su nombre completo al modelo.
	    if (user != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.EXTRA_INFO, user.getId() }));
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());
		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.EXTRA_INFO, null }));
	    }
	    // Devolvemos el modelo y la vista home.
	    modelAndView.setViewName(PathResources.HOME);
	    return modelAndView;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/home/extraInfo' through of a
     * get HTTP method.
     * 
     * @return a new view with the extraInfo page.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.HOME + CommonsResources.SLASH
	    + PathResources.EXTRA_INFO }, method = RequestMethod.GET)
    public ModelAndView moreInfo() {
	try {
	    // inicializamos la variable que albergará el modelo e intentamos recuperar el
	    // usuario registrado.
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
	    // Si existe un usuario registrado, añadimos su nombre completo al modelo.
	    if (user != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.EXTRA_INFO, user.getId() }));
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());
		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.EXTRA_INFO, null }));
	    }
	    // Devolvemos el modelo y la vista extraInfo.
	    modelAndView.setViewName(PathResources.EXTRA_INFO);
	    return modelAndView;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/error' through of a get HTTP
     * method.
     * 
     * @return the general error view.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.ERROR }, method = RequestMethod.GET)
    public ModelAndView error() {
	try {
	    ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName(PathResources.ERROR);
	    return modelAndView;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that maps the request for the resource '/copyright' through of a get
     * HTTP method.
     * 
     * @return a view with the attributions and license information.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.COPYRIGHT }, method = RequestMethod.GET)
    public ModelAndView copyright() {
	try {
	    // inicializamos la variable que albergará el modelo e intentamos recuperar el
	    // usuario registrado.
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
	    // Si existe un usuario registrado, añadimos su nombre completo al modelo.
	    if (user != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.COPYRIGHT, user.getId() }));
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());
		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.COPYRIGHT, null }));
	    }
	    // Devolvemos el modelo y la vista de copyright.
	    modelAndView.setViewName(PathResources.COPYRIGHT);
	    return modelAndView;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }
}
