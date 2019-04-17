package us.etsii.fvt.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import us.etsii.fvt.domains.Notification;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.UserAccessManager;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;
import us.etsii.fvt.utils.exceptions.UnauthorizedAccessException;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the notification controller.
 */
@SessionAttributes({ Parameters.INFOS, Parameters.PROBLEMS })
@Controller
public class NotificationController {

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
    private static final Logger LOGGER = LogManager.getLogger(NotificationController.class);

    /**
     * Constant that defines the minimum current page number.
     */
    private static final Integer MIN_CURRENT_PAGE = 1;

    /**
     * Constant that defines the minimum page size number.
     */
    private static final Integer MIN_PAGE_SIZE = 1;

    /**
     * Constant that defines the maximum page size number.
     */
    private static final Integer MAX_PAGE_SIZE = 500;

    /**
     * Constant that defines the default page size number.
     */
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * Method that persists the information messages between views.
     * 
     * @return a list of strings with the info messages.
     */
    @ModelAttribute(Parameters.INFOS)
    public List<String> infos() {
	return new ArrayList<String>();
    }

    /**
     * Method that persists the error messages between views.
     * 
     * @return a list of strings with the error messages.
     */
    @ModelAttribute(Parameters.PROBLEMS)
    public List<String> problems() {
	return new ArrayList<String>();
    }

    /**
     * Method that maps the request for the resource '/notification' through of a
     * get HTTP method.
     * 
     * @param page          current page to show.
     * @param size          Number of element to show by page.
     * @param infos         List of information messages to show.
     * @param problems      List of error messages to show.
     * @param sessionStatus Indicates the status of the session.
     * @return a new view with the list of notifications.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.NOTIFICATION }, method = RequestMethod.GET)
    public ModelAndView notification(@RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size, @ModelAttribute(Parameters.INFOS) List<String> infos,
	    @ModelAttribute(Parameters.PROBLEMS) List<String> problems, SessionStatus sessionStatus) {
	try {
	    ModelAndView modelAndView = new ModelAndView();

	    // Recuperamos el usuario
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());
		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.NOTIFICATION, user.getId() }));

		// Añadimos el usuario al modelo.
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Si hay mensajes de información que mostrar, los añadimos.
		if (infos != null && !infos.isEmpty()) {
		    modelAndView.addObject(Parameters.INFOS, infos);
		    // eliminamos la variable de la sesión para que, al recargar la vista, no vuelva
		    // a aparecer la alerta.
		    sessionStatus.setComplete();
		}

		// Si hay mensajes de errores que mostrar, los añadimos.
		if (problems != null && !problems.isEmpty()) {
		    modelAndView.addObject(Parameters.PROBLEMS, problems);
		    // eliminamos la variable de la sesión para que, al recargar la vista, no vuelva
		    // a aparecer la alerta.
		    sessionStatus.setComplete();
		}

		// Recuperamos los parámetros de paginación (o los inicializamos).
		int currentPage = page.orElse(MIN_CURRENT_PAGE);
		if (currentPage < MIN_CURRENT_PAGE) {
		    currentPage = MIN_CURRENT_PAGE;
		}
		int pageSize = size.orElse(DEFAULT_PAGE_SIZE);
		if (pageSize < MIN_PAGE_SIZE) {
		    pageSize = MIN_PAGE_SIZE;
		} else if (pageSize > MAX_PAGE_SIZE) {
		    pageSize = MAX_PAGE_SIZE;
		}

		// Recuperamos la página de vulnerabilidades a mostrar.
		Page<Notification> notsPage = notificationService.findAllNotificationsPaginated(PageRequest
			.of(currentPage - 1, pageSize, Sort.by(Sort.Direction.ASC, Parameters.CREATION_DATE)),
			user.getId());

		// Marcamos como leidas aquellas notificaciones que se van a mostrar.
		if (notsPage != null) {
		    notificationService.notificationRead(notsPage.getContent());
		}

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

		// Añadimos la página al modelo.
		modelAndView.addObject(Parameters.NOTS_PAGE, notsPage);

		// Devolvemos el modelo.
		modelAndView.setViewName(PathResources.NOTIFICATION);
		return modelAndView;
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_NOT_ACCESSIBLE, true));
		throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_ACCESSIBLE, false));
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/notification/remove/{id}',
     * which take care of remove the notification from database.
     * 
     * @param id         Notification identifier.
     * @param attributes Parameter that stores attributes into the user session.
     * @return a redirected view with the list of notification.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.NOTIFICATION + CommonsResources.SLASH + PathResources.REMOVE
	    + CommonsResources.SLASH + Parameters.ID_PARAM)
    public ModelAndView removeNotification(@PathVariable String id, RedirectAttributes attributes) {
	try {
	    // Si el Id es distinto de null...
	    if (id != null) {

		// Buscamos la notificación en BBDD.
		Notification notification = notificationService.findById(id);

		// Si existe en BBDD...
		if (notification != null) {

		    // Comprobamos que el usuario tiene permisos para realizar la operación.
		    User user = userService
			    .findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		    UserAccessManager.userAccess(user.getId(), notification.getUser().getId());

		    // Eliminamos la notificación.
		    notificationService.remove(id);

		    // Mostramos mensaje de confirmación
		    List<String> infos = new ArrayList<String>();
		    infos.add(I18n.getResource(LanguageKeys.NOTIFICATION_REMOVED_SUCCESSFULLY, false));
		    attributes.addFlashAttribute(Parameters.INFOS, infos);
		} else {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_NOTIFICATION_NOT_FOUND, true,
			    new Object[] { id }));
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_NOTIFICATION_ERROR_ID_NULL, true));
		List<String> problems = new ArrayList<String>();
		problems.add(I18n.getResource(LanguageKeys.NOTIFICATION_REMOVED_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    }
	    return new ModelAndView(
		    Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.NOTIFICATION);
	} catch (UnauthorizedAccessException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.EXCEPTION_THROWN, true, new Object[] { e }));
	    return new ModelAndView(PathResources.UNAUTHORIZED);
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/notification/removeAll'
     * through of a get HTTP method.
     * 
     * @param attributes Parameter that stores attributes into the user session.
     * @return a redirected view with the list of notification.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.NOTIFICATION + CommonsResources.SLASH
	    + PathResources.REMOVE_ALL }, method = RequestMethod.GET)
    public ModelAndView removeAll(RedirectAttributes attributes) {
	try {

	    // Recuperamos el usuario
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());

		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}
		// Eliminamos todas las notificaciones del usuario.
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_ALL_NOTIFICATION, true,
			new Object[] { user.getId() }));
		notificationService.removeAll(user.getId());

		// Añadimos mensajes de confirmación.
		List<String> infos = new ArrayList<String>();
		infos.add(I18n.getResource(LanguageKeys.ALL_NOTIFICATION_REMOVED_SUCCESSFULLY, false));
		attributes.addFlashAttribute(Parameters.INFOS, infos);

		// Redirigimos a la vista de notificaciones.
		return new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
			+ PathResources.NOTIFICATION);
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_NOT_ACCESSIBLE, true));
		throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_ACCESSIBLE, false));
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

}
