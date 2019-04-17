package us.etsii.fvt.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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

import us.etsii.fvt.domains.Alarm;
import us.etsii.fvt.domains.Tracking;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.services.AlarmService;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.TrackingService;
import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.UserAccessManager;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;
import us.etsii.fvt.utils.exceptions.ResourceNotAvailableException;
import us.etsii.fvt.utils.exceptions.UnauthorizedAccessException;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the tracking controller.
 */
@SessionAttributes({ Parameters.INFOS, Parameters.PROBLEMS })
@Controller
public class TrackingController {

    /**
     * User service attribute.
     */
    @Autowired
    private UserService userService;

    /**
     * tracking service attribute.
     */
    @Autowired
    private TrackingService trackingService;

    /**
     * alarm service attribute.
     */
    @Autowired
    private AlarmService alarmService;

    /**
     * Notification service attribute.
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(TrackingController.class);

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
    private static final Integer MAX_PAGE_SIZE = 100;

    /**
     * Constant that defines the default page size number.
     */
    private static final Integer DEFAULT_PAGE_SIZE = 15;

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
     * Method that maps the request for the resource '/tracking' through of a get
     * HTTP method.
     * 
     * @param infos         List of information messages to show.
     * @param problems      List of error messages to show.
     * @param sessionStatus Indicates the status of the session.
     * @param page          current page to show.
     * @param size          Number of element to show by page.
     * @return a new view with the tracking list.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.TRACKING }, method = RequestMethod.GET)
    public ModelAndView tracking(@ModelAttribute(Parameters.INFOS) List<String> infos,
	    @ModelAttribute(Parameters.PROBLEMS) List<String> problems, SessionStatus sessionStatus,
	    @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {
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
			new Object[] { PathResources.TRACKING, user.getId() }));
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

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

		// Recuperamos la página de seguimientos a mostrar.
		Page<Tracking> trackingsPage = trackingService
			.findAllTrackingsPaginated(PageRequest.of(currentPage - 1, pageSize), user.getId());

		// Añadimos la lista de trackings al modelo
		modelAndView.addObject(Parameters.TRACKINGS, trackingsPage);

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

		// Devolvemos el modelo
		modelAndView.setViewName(PathResources.TRACKING);
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
     * Method that maps the request for the resource '/tracking/createTracking'
     * through of a get HTTP method.
     * 
     * @param infos         List of information messages to show.
     * @param problems      List of error messages to show.
     * @param sessionStatus Indicates the status of the session.
     * @return a new view with the form to create a new tracking.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.TRACKING + CommonsResources.SLASH
	    + PathResources.CREATE_TRACKING }, method = RequestMethod.GET)
    public ModelAndView createTracking(@ModelAttribute(Parameters.INFOS) List<String> infos,
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
			new Object[] { PathResources.CREATE_TRACKING, user.getId() }));
		// Añadimos los parametros al modelo.
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

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

		modelAndView.setViewName(PathResources.CREATE_TRACKING);
		// Devolvemos la vista.
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
     * Method that maps the request for the resource '/tracking/createTracking'
     * through of a post HTTP method and save the form in the database.
     * 
     * @param tracking   New tracking object.
     * @param errors     Binding object to return errors to form.
     * @param attributes Parameter that stores attributes into the user session.
     * @return a redirected view of the tracking list.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.TRACKING + CommonsResources.SLASH
	    + PathResources.CREATE_TRACKING, method = RequestMethod.POST)
    public ModelAndView createNewTracking(@Valid Tracking tracking, BindingResult errors,
	    RedirectAttributes attributes) {
	try {
	    ModelAndView modelAndView = new ModelAndView();
	    // Recuperamos el usuario.
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());
		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}
		tracking.setAlarms(new HashSet<Alarm>());
		tracking.setUser(user);
		if (errors.hasErrors()) {
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_CREATE_TRACKING_ERROR, true));
		    // Se añaden los parametros de errores y se vuelve a cargar la misma página.
		    if (tracking != null && tracking.getTrackingName() != null) {
			modelAndView.addObject(Parameters.TRACKING_NAME, tracking.getTrackingName());
		    }
		    if (tracking != null && tracking.getSoftwareName() != null) {
			modelAndView.addObject(Parameters.SOFTWARE_NAME, tracking.getSoftwareName());
		    }
		    modelAndView.addObject(Parameters.CURRENT_USER, user);
		    modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());
		    boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		    modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);
		    modelAndView.addObject(Parameters.ERRORS, errors.getAllErrors());
		    modelAndView.setViewName(PathResources.CREATE_TRACKING);

		} else {
		    trackingService.saveTracking(tracking);
		    List<String> infos = new ArrayList<String>();
		    infos.add(I18n.getResource(LanguageKeys.TRACKING_CREATED_SUCCESSFULLY, false));
		    attributes.addFlashAttribute(Parameters.INFOS, infos);
		    modelAndView.addObject(PathResources.TRACKING, new Tracking());
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.TRACKING);
		}
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
     * Method that maps the post request for the resource '/tracking/edit/{id}' and
     * show the data of the tracking in order to be edited.
     * 
     * @param id         Tracking identifier.
     * @param tracking   New tracking object.
     * @param errors     Binding object to return errors to form.
     * @param attributes Parameter that stores attributes into the user session.
     * @param page       current page to show.
     * @param size       Number of element to show by page.
     * @return a new view with the tracking data.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.TRACKING + CommonsResources.SLASH + PathResources.EDIT
	    + CommonsResources.SLASH + Parameters.ID_PARAM)
    public ModelAndView editTracking(@PathVariable String id, @Valid Tracking tracking, BindingResult errors,
	    RedirectAttributes attributes, @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {

	// Instanciamos la lista que contendrán los mensajes de la interfaz de usuario.
	List<String> problems = new ArrayList<String>();
	List<String> infos = new ArrayList<String>();

	try {
	    // Si el ID y el seguimiento nuevo no son nulos, realizamos la modificación.
	    if (id != null && tracking != null) {

		// Recuperamos el seguimiento
		Tracking t = trackingService.findById(id);

		if (t == null) {
		    throw new ResourceNotAvailableException(
			    I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { id }));
		}

		// Comprobamos que el usuario tiene permisos para realizar la operación.
		User user = userService
			.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		UserAccessManager.userAccess(user.getId(), t.getUser().getId());

		// Si hay errores en los parámetros de entrada, los lanzamos.
		if (errors.hasErrors()) {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_TRACKING_EDIT_HAS_ERRORS, true,
			    new Object[] { id, errors.getErrorCount() }));
		    attributes.addFlashAttribute(Parameters.ERRORS, errors.getAllErrors());
		    return new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
			    + PathResources.TRACKING);
		}

		// Editamos el seguimiento
		trackingService.editTracking(id, tracking);

		// Añadimos mensaje de confirmación
		infos.add(I18n.getResource(LanguageKeys.TRACKING_EDITED_SUCCESSFULLY, false));
		attributes.addFlashAttribute(Parameters.INFOS, infos);
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_TRACKING_OR_ID_NULL, true,
			new Object[] { id, tracking }));
		problems.add(I18n.getResource(LanguageKeys.TRACKING_EDITED_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    }

	    ModelAndView modelAndView = new ModelAndView(
		    Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);

	    // Si es necesario, añadimos los parámetros de paginación.
	    if (page.isPresent()) {
		modelAndView.addObject(Parameters.PAGE, page.get());
	    }
	    if (size.isPresent()) {
		modelAndView.addObject(Parameters.SIZE, size.get());
	    }
	    return modelAndView;

	} catch (ResourceNotAvailableException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.EXCEPTION_THROWN, true, new Object[] { e }));
	    problems.add(I18n.getResource(LanguageKeys.TRACKING_NOT_EXISTS, false));
	    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    return new ModelAndView(
		    Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);
	} catch (UnauthorizedAccessException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.EXCEPTION_THROWN, true, new Object[] { e }));
	    return new ModelAndView(PathResources.UNAUTHORIZED);
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/tracking/remove/{id}', which
     * take care of remove the tracking from database.
     * 
     * @param id         Tracking identifier.
     * @param attributes Parameter that stores attributes into the user session.
     * @param page       current page to show.
     * @param size       Number of element to show by page.
     * @return a redirected view with the list of tracking.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.TRACKING + CommonsResources.SLASH + PathResources.REMOVE
	    + CommonsResources.SLASH + Parameters.ID_PARAM)
    public ModelAndView removeTracking(@PathVariable String id, RedirectAttributes attributes,
	    @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {
	try {
	    // Si el Id es distinto de null...
	    if (id != null) {
		// Buscamos el seguimiento en BBDD.
		Tracking tracking = trackingService.findById(id);
		// Si existe en BBDD...
		if (tracking != null) {

		    // Comprobamos que el usuario tiene permisos para realizar la operación.
		    User user = userService
			    .findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		    UserAccessManager.userAccess(user.getId(), tracking.getUser().getId());

		    // Recuperamos el conjunto de alarmas del seguimiento que vamos a eliminar.
		    Set<Alarm> alarms = trackingService.findById(id).getAlarms();
		    // Eliminamos todas las alarmas que pertenecen al seguimiento.
		    alarmService.removeAll(alarms);
		    // Eliminamos todas las notificaciones relacionadas con el seguimiento.
		    notificationService.removeAllByTracking(id);
		    // Eliminamos el seguimiento.
		    trackingService.remove(id);
		    // Mostramos mensaje de confirmación
		    List<String> infos = new ArrayList<String>();
		    infos.add(I18n.getResource(LanguageKeys.TRACKING_REMOVED_SUCCESSFULLY, false));
		    attributes.addFlashAttribute(Parameters.INFOS, infos);
		} else {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_TRACKING_NOT_FOUND, true,
			    new Object[] { id }));
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_TRACKING_ERROR_ID_NULL, true));
		List<String> problems = new ArrayList<String>();
		problems.add(I18n.getResource(LanguageKeys.TRACKING_REMOVED_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    }

	    ModelAndView modelAndView = new ModelAndView(
		    Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);

	    // Si es necesario, añadimos los parámetros de paginación.
	    if (page.isPresent()) {
		modelAndView.addObject(Parameters.PAGE, page.get());
	    }
	    if (size.isPresent()) {
		modelAndView.addObject(Parameters.SIZE, size.get());
	    }
	    return modelAndView;

	} catch (UnauthorizedAccessException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.EXCEPTION_THROWN, true, new Object[] { e }));
	    return new ModelAndView(PathResources.UNAUTHORIZED);
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

}
