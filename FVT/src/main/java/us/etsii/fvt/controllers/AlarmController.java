package us.etsii.fvt.controllers;

import java.util.ArrayList;
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
import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.domains.Tracking;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.services.AlarmService;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.SettingService;
import us.etsii.fvt.services.TrackingService;
import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.UserAccessManager;
import us.etsii.fvt.utils.Validations;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;
import us.etsii.fvt.utils.enums.Period;
import us.etsii.fvt.utils.exceptions.ResourceNotAvailableException;
import us.etsii.fvt.utils.exceptions.UnauthorizedAccessException;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the controller of the alarm view.
 */
@SessionAttributes({ Parameters.INFOS, Parameters.PROBLEMS, Parameters.DEFAULT_ALARM })
@Controller
public class AlarmController {

    /**
     * User service attribute.
     */
    @Autowired
    private UserService userService;

    /**
     * Alarm service attribute.
     */
    @Autowired
    private AlarmService alarmService;

    /**
     * Tracking service attribute.
     */
    @Autowired
    private TrackingService trackingService;

    /**
     * Setting service attribute.
     */
    @Autowired
    private SettingService settingService;

    /**
     * Notification service attribute.
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(AlarmController.class);

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
     * Method that persists the default alarm between views.
     * 
     * @return a default alarm object.
     */
    @ModelAttribute(Parameters.DEFAULT_ALARM)
    public Alarm defaultAlarm() {
	return new Alarm();
    }

    /**
     * Method that maps the request for the resource '/alarms/{id}' through of a get
     * HTTP method.
     * 
     * @param id            Tracking identifier whose alarms belong.
     * @param attributes    Parameter that stores attributes into the user session.
     * @param infos         List of information messages to show.
     * @param problems      List of error messages to show.
     * @param sessionStatus Indicates the status of the session.
     * @param page          current page to show.
     * @param size          Number of element to show by page.
     * @return a new view with the list of alarms of the given tracking.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH
	    + Parameters.ID_PARAM, method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView alarmsTracking(@PathVariable String id, RedirectAttributes attributes,
	    @ModelAttribute(Parameters.INFOS) List<String> infos,
	    @ModelAttribute(Parameters.PROBLEMS) List<String> problems, SessionStatus sessionStatus,
	    @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {
	try {
	    ModelAndView modelAndView = new ModelAndView();
	    // Recuperamos el usurio actual.
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());
		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.ALARMS + CommonsResources.SLASH + id, user.getId() }));
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

		// Recuperamos el conjunto de alarmas del seguimiento.
		Tracking tracking = trackingService.findById(id);
		if (tracking != null) {
		    // Comprobamos que el usuario es el propietario de los datos solicitados.
		    UserAccessManager.userAccess(user.getId(), tracking.getUser().getId());
		    Set<Alarm> alarms = tracking.getAlarms();
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SHOW_ALARM, true,
			    new Object[] { alarms.size(), tracking.getId() }));

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

		    // Recuperamos la página de alarmas a mostrar.
		    Page<Alarm> alarmsPage = alarmService
			    .findAllAlarmsPaginated(PageRequest.of(currentPage - 1, pageSize), alarms);

		    // Añadimos el conjunto de alarmas y el ID del seguimiento al modelo.
		    modelAndView.addObject(Parameters.ALARMS, alarmsPage);
		    modelAndView.addObject(Parameters.TRACKING_ID, id);

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

		    // Recuperamos la lista de periodos y la añadimos al modelo.
		    List<Integer> periods = Period.getAllValues();
		    modelAndView.addObject(Parameters.PERDIODS, periods);

		    modelAndView.setViewName(PathResources.ALARM);
		} else {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_TRACKING_NULL, true,
			    new Object[] { id }));
		    List<String> problemsAlarms = new ArrayList<String>();
		    problemsAlarms.add(I18n.getResource(LanguageKeys.ALARMS_ERROR_TRACKING_NULL, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problemsAlarms);
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.TRACKING);
		}
		return modelAndView;
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_NOT_ACCESSIBLE, true));
		throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_ACCESSIBLE, false));
	    }
	} catch (UnauthorizedAccessException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.EXCEPTION_THROWN, true, new Object[] { e }));
	    return new ModelAndView(PathResources.UNAUTHORIZED);
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Method that maps the request for the resource '/alarms/createAlarm/{id}'
     * through of a get HTTP method.
     * 
     * @param id            Tracking identifier.
     * @param infos         List of information messages to show.
     * @param problems      List of error messages to show.
     * @param defaultAlarm  Default alarm attribute.
     * @param attributes    Parameter that stores attributes into the user session.
     * @param sessionStatus Indicates the status of the session.
     * @return a new view with the form to create a new alarm.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH
	    + PathResources.CREATE_ALARM + CommonsResources.SLASH + Parameters.ID_PARAM, method = RequestMethod.GET)
    public ModelAndView CreateNewALarm(@PathVariable String id, @ModelAttribute(Parameters.INFOS) List<String> infos,
	    @ModelAttribute(Parameters.PROBLEMS) List<String> problems,
	    @ModelAttribute(Parameters.DEFAULT_ALARM) Alarm defaultAlarm, RedirectAttributes attributes,
	    SessionStatus sessionStatus) {

	// Instanciamos la lista que contendrán los mensajes de la interfaz de usuario.
	List<String> problemList = new ArrayList<String>();

	try {
	    ModelAndView modelAndView = new ModelAndView();
	    // Añadimos los parámetros del usuario
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());
		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}

		// Recuperamos el seguimiento
		Tracking t = trackingService.findById(id);

		// Si el seguimiento no existe, lanzamos excepción.
		if (t == null) {
		    throw new ResourceNotAvailableException(
			    I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { id }));
		}

		// Comprobamos que el usuario es el propietario de los datos solicitados.
		UserAccessManager.userAccess(user.getId(), t.getUser().getId());

		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.ALARMS + CommonsResources.SLASH + PathResources.CREATE_ALARM
				+ CommonsResources.SLASH + id, user.getId() }));
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

		boolean setComplete = false;
		// Si hay mensajes de información que mostrar, los añadimos.
		if (infos != null && !infos.isEmpty()) {
		    modelAndView.addObject(Parameters.INFOS, infos);
		    // eliminamos la variable de la sesión para que, al recargar la vista, no vuelva
		    // a aparecer la alerta.
		    setComplete = true;
		}

		// Si hay mensajes de errores que mostrar, los añadimos.
		if (problems != null && !problems.isEmpty()) {
		    modelAndView.addObject(Parameters.PROBLEMS, problems);
		    // eliminamos la variable de la sesión para que, al recargar la vista, no vuelva
		    // a aparecer la alerta.
		    setComplete = true;
		}

		// Si se ha recibido la configuración de la alarma por defecto, la añadimos.
		if (defaultAlarm != null) {
		    modelAndView.addObject(Parameters.DEFAULT_ALARM, defaultAlarm);
		    // eliminamos la variable de la sesión para que, al recargar la vista, no vuelva
		    // a aparecer la alerta.
		    setComplete = true;
		}

		// Si es necesario reiniciar la variable de sesión, lo hacemos.
		if (setComplete) {
		    sessionStatus.setComplete();
		}

		// Si el id del seguimiento no es nulo, lo añadimos como parámetro y mostramos
		// la nueva vista.
		if (id != null) {
		    modelAndView.addObject(Parameters.TRACKING_ID, id);
		    modelAndView.setViewName(PathResources.CREATE_ALARM);
		} else {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_TRACKING_ID_NULL, true));
		    problemList.add(I18n.getResource(LanguageKeys.CREATE_ALARM_ERROR, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problemList);
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.TRACKING);
		}

		// Recuperamos la lista de periodos y la añadimos al modelo.
		List<Integer> periods = Period.getAllValues();
		modelAndView.addObject(Parameters.PERDIODS, periods);

		return modelAndView;
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_NOT_ACCESSIBLE, true));
		throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_ACCESSIBLE, false));
	    }
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
     * Method that maps the request for the resource '/alarm/createAlarm/{id}'
     * through of a post HTTP method and save the new alarm in database.
     * 
     * @param id         Tracking identifier.
     * @param alarm      New alarm object to save.
     * @param errors     Binding object to return errors to form.
     * @param attributes Parameter that stores attributes into the user session.
     * @return the general view with the list of alarms.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH
	    + PathResources.CREATE_ALARM + CommonsResources.SLASH + Parameters.ID_PARAM, method = RequestMethod.POST)
    public ModelAndView createNewAlarm(@PathVariable String id, @Valid Alarm alarm, BindingResult errors,
	    RedirectAttributes attributes) {

	// Instanciamos la lista que contendrán los mensajes de la interfaz de usuario.
	List<String> problems = new ArrayList<String>();
	List<String> infos = new ArrayList<String>();

	try {
	    ModelAndView modelAndView = new ModelAndView();

	    // Si ambos parámetros son válidos...
	    boolean validAlarm = Validations.checkAlarm(alarm);
	    if (id != null && validAlarm) {

		// Recuperamos el seguimiento
		Tracking t = trackingService.findById(id);

		// Si el seguimiento no existe, lanzamos excepción.
		if (t == null) {
		    throw new ResourceNotAvailableException(
			    I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { id }));
		}

		// Comprobamos que el usuario tiene permisos para realizar la operación.
		User user = userService
			.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		UserAccessManager.userAccess(user.getId(), t.getUser().getId());

		// Comprobamos que no se hayan producido errores durante la validación de los
		// parámetros.
		if (errors.hasErrors()) {
		    problems.add(I18n.getResource(LanguageKeys.CREATE_ALARM_ERROR_INVALID_PARAMS, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH
			    + PathResources.CREATE_ALARM + CommonsResources.SLASH + id);
		} else {
		    alarm.setId(null);

		    // Guardamos la alarma
		    boolean result = alarmService.saveAlarm(alarm);

		    // Si se ha producido algún error durante el proceso de guardado...
		    if (!result) {
			problems.add(I18n.getResource(LanguageKeys.CREATE_ALARM_ERROR, false));
			attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    }

		    // Actualizamos el conjunto de alarmas del tracking
		    boolean success = trackingService.AddAlarm(id, alarm);
		    if (!success) {
			LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_CREATED, true,
				new Object[] { id, alarm.getId() }));
			problems.add(I18n.getResource(LanguageKeys.CREATE_ALARM_ERROR, false));
			attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    } else {
			LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_CREATED_SUCCESSFULLY, true,
				new Object[] { alarm.getId() }));
			infos.add(I18n.getResource(LanguageKeys.CREATE_ALARM_SUCCESS, false));
			attributes.addFlashAttribute(Parameters.INFOS, infos);
		    }
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + id);
		}
	    } else {
		if (id == null) {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_TRACKING_ID_NULL, true));
		    problems.add(I18n.getResource(LanguageKeys.CREATE_ALARM_ERROR, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.TRACKING);
		} else if (!validAlarm) {
		    problems.add(I18n.getResource(LanguageKeys.CREATE_ALARM_ERROR_INVALID_PARAMS, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
			    + CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH
			    + PathResources.CREATE_ALARM + CommonsResources.SLASH + id);
		}
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
     * Method that maps the request for the resource
     * '/alarm/remove/{id}/{trackingId}', which take care of remove the alarm from
     * database.
     * 
     * @param id         Alarm identifier to remove.
     * @param trackingId Tracking identifier.
     * @param attributes Parameter that stores attributes into the user session.
     * @param page       current page to show.
     * @param size       Number of element to show by page.
     * @return a redirected view with the list of alarms.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + PathResources.REMOVE
	    + CommonsResources.SLASH + Parameters.ID_PARAM + CommonsResources.SLASH + Parameters.TRACKING_ID_PARAM)
    public ModelAndView removeAlarm(@PathVariable String id, @PathVariable String trackingId,
	    RedirectAttributes attributes, @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {

	// Instanciamos la lista que contendrán los mensajes de la interfaz de usuario.
	List<String> problems = new ArrayList<String>();
	List<String> infos = new ArrayList<String>();

	try {

	    // Si el seguimiento es nulo, lanzamos error.
	    if (trackingId == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_REMOVE_TRACKING_ID_NULL, true));
		problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_REDIRECT_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);
	    }

	    // Recuperamos el seguimiento
	    Tracking t = trackingService.findById(trackingId);

	    // Si el seguimiento no existe, lanzamos excepción.
	    if (t == null) {
		throw new ResourceNotAvailableException(
			I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { trackingId }));
	    }

	    // Comprobamos que el usuario tiene permisos para realizar la operación.
	    User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
	    UserAccessManager.userAccess(user.getId(), t.getUser().getId());

	    // Si el identificador de alarma no es nulo, seguimos con la operación.
	    if (id != null) {
		Alarm alarm = alarmService.findById(id);
		if (alarm != null) {
		    // Eliminamos la referencia a la alarma
		    Tracking tracking = trackingService.findById(trackingId);
		    tracking.getAlarms().remove(alarm);
		    trackingService.editTracking(trackingId, tracking);
		    // Eliminamos la alarma
		    boolean success = alarmService.remove(id);
		    if (success) {
			LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_REMOVED_SUCCESSFULLY, true,
				new Object[] { id }));
			infos.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_SUCCESS, false));
			attributes.addFlashAttribute(Parameters.INFOS, infos);
		    } else {
			LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_REMOVED_FAILURE, true,
				new Object[] { id }));
			problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_ERROR, false));
			attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    }
		} else {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_NOT_EXISTS, true,
			    new Object[] { id }));
		    problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_ERROR, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_ID_NULL, true));
		problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    }

	    ModelAndView modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
		    + CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + trackingId);

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
     * Method that maps the request for the resource
     * '/alarm/edit/{id}/{trackingId}', which take care of edit the alarm from
     * database.
     * 
     * @param id         Alarm identifier.
     * @param trackingId Tracking identifier.
     * @param alarm      New alarm object to replace for the old one.
     * @param errors     Binding object to return errors to form.
     * @param attributes Parameter that stores attributes into the user session.
     * @param page       current page to show.
     * @param size       Number of element to show by page.
     * @return a redirected view with the list of alarms.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + PathResources.EDIT
	    + CommonsResources.SLASH + Parameters.ID_PARAM + CommonsResources.SLASH + Parameters.TRACKING_ID_PARAM)
    public ModelAndView editAlarm(@PathVariable String id, @PathVariable String trackingId, @Valid Alarm alarm,
	    BindingResult errors, RedirectAttributes attributes, @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {

	// Instanciamos la lista que contendrán los mensajes de la interfaz de usuario.
	List<String> problems = new ArrayList<String>();
	List<String> infos = new ArrayList<String>();

	try {

	    // Si el identificador del seguimiento es nulo, lanzamos error.
	    if (trackingId == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_REMOVE_TRACKING_ID_NULL, true));
		problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_REDIRECT_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);
	    }

	    // Recuperamos el seguimiento
	    Tracking t = trackingService.findById(trackingId);

	    // Si el seguimiento no existe, lanzamos excepción.
	    if (t == null) {
		throw new ResourceNotAvailableException(
			I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { trackingId }));
	    }

	    // Comprobamos que el usuario tiene permisos para realizar la operación.
	    User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
	    UserAccessManager.userAccess(user.getId(), t.getUser().getId());

	    // Si hay errores en los parámetros de entrada, lanzamos error
	    if (errors.hasErrors()) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_EDIT_HAS_ERRORS, false,
			new Object[] { trackingId, errors.getErrorCount() }));
		attributes.addFlashAttribute(Parameters.ERRORS, errors.getAllErrors());
		return new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
			+ PathResources.ALARMS + CommonsResources.SLASH + trackingId);
	    }

	    // Si el identificador de alarma es válido, seguimos...
	    ModelAndView modelAndView = new ModelAndView();
	    if (id != null && alarm != null) {
		boolean success = alarmService.editAlarm(id, alarm, false);
		if (success) {
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_EDITED_SUCCESSFULLY, true,
			    new Object[] { id }));
		    infos.add(I18n.getResource(LanguageKeys.EDIT_ALARM_SUCCESS, false));
		    attributes.addFlashAttribute(Parameters.INFOS, infos);
		} else {
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_EDITED_FAILURE, true,
			    new Object[] { id }));
		    problems.add(I18n.getResource(LanguageKeys.EDIT_ALARM_ERROR, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_EDIT_ALARM_OR_ID_NULL, true,
			new Object[] { id, alarm }));
		problems.add(I18n.getResource(LanguageKeys.EDIT_ALARM_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    }
	    modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
		    + PathResources.ALARMS + CommonsResources.SLASH + trackingId);

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
     * Method that maps the request for the resource
     * '/alarms/enable/{id}/{trackingId}/{enableDisable}', which take care of enable
     * or disable the alarm from database.
     * 
     * @param id            Alarm identifier.
     * @param trackingId    Tracking identifier.
     * @param enableDisable Boolean that indicates if the alarm must be enabled
     *                      (true) or disabled (false).
     * @param attributes    Parameter that stores attributes into the user session.
     * @param page          current page to show.
     * @param size          Number of element to show by page.
     * @return a redirected view with the list of alarms.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + PathResources.ENABLE
	    + CommonsResources.SLASH + Parameters.ID_PARAM + CommonsResources.SLASH + Parameters.TRACKING_ID_PARAM
	    + CommonsResources.SLASH + Parameters.ENABLE_DISABLE)
    public ModelAndView enableAlarm(@PathVariable String id, @PathVariable String trackingId,
	    @PathVariable boolean enableDisable, RedirectAttributes attributes,
	    @RequestParam(Parameters.PAGE) Optional<Integer> page,
	    @RequestParam(Parameters.SIZE) Optional<Integer> size) {

	// Instanciamos la lista que contendrán los mensajes de la interfaz de usuario.
	List<String> problems = new ArrayList<String>();

	try {
	    if (trackingId == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_REMOVE_TRACKING_ID_NULL, true));
		problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_REDIRECT_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);
	    }

	    // Recuperamos el seguimiento
	    Tracking t = trackingService.findById(trackingId);

	    // Si el seguimiento no existe, lanzamos excepción.
	    if (t == null) {
		throw new ResourceNotAvailableException(
			I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { trackingId }));
	    }

	    // Comprobamos que el usuario tiene permisos para realizar la operación.
	    User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
	    UserAccessManager.userAccess(user.getId(), t.getUser().getId());

	    if (id != null) {
		Alarm alarm = alarmService.findById(id);
		if (alarm == null) {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_NOT_EXISTS, true,
			    new Object[] { id }));
		    problems.add(I18n.getResource(LanguageKeys.ENABLE_ALARM_ERROR, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);

		} else {
		    alarm.setEnabled(enableDisable);
		    boolean success = alarmService.editAlarm(id, alarm, true);
		    if (success) {
			LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_ENABLED_SUCCESSFULLY, true,
				new Object[] { id }));
		    } else {
			LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_ENABLED_FAILURE, true,
				new Object[] { id }));
			problems.add(I18n.getResource(LanguageKeys.ENABLE_ALARM_ERROR, false));
			attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    }
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_ID_NULL, true));
		problems.add(I18n.getResource(LanguageKeys.ENABLE_ALARM_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
	    }

	    ModelAndView modelAndView = new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON
		    + CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + trackingId);

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
     * Method that maps the request for the resource
     * '/alarms/createAlarm/defaultAlarm/{trackingId}', which take care of use the
     * values of the default alarm in the creation process.
     * 
     * @param trackingId Tracking identifier.
     * @param attributes Parameter that stores attributes into the user session.
     * @return a redirected view with the creation alarm form and the fields values
     *         of the default alarm.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.ALARMS + CommonsResources.SLASH + PathResources.CREATE_ALARM
	    + CommonsResources.SLASH + Parameters.DEFAULT_ALARM + CommonsResources.SLASH + Parameters.TRACKING_ID_PARAM)
    public ModelAndView useDefaultAlarm(@PathVariable String trackingId, RedirectAttributes attributes) {

	// iniciamos las variables que vamos a necesitar.
	List<String> problems = new ArrayList<String>();
	ModelAndView modelAndView = new ModelAndView();

	try {
	    // Si el id del seguimiento es nulo, lanzamos error y volvemos a la vista de
	    // seguimientos.
	    if (trackingId == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_REMOVE_TRACKING_ID_NULL, true));
		problems.add(I18n.getResource(LanguageKeys.REMOVE_ALARM_REDIRECT_ERROR, false));
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.TRACKING);
	    }

	    // Recuperamos el seguimiento
	    Tracking t = trackingService.findById(trackingId);

	    // Si el seguimiento no existe, lanzamos excepción.
	    if (t == null) {
		throw new ResourceNotAvailableException(
			I18n.getResource(LanguageKeys.ERROR_NO_OBJECT_FOUND, false, new Object[] { trackingId }));
	    }

	    // Comprobamos que el usuario tiene permisos para realizar la operación.
	    User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
	    UserAccessManager.userAccess(user.getId(), t.getUser().getId());

	    // Recuperamos la configuración del usuario
	    Setting setting = settingService.findByUserId(user.getId());
	    if (setting == null) {
		LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_NULL2, true,
			new Object[] { user.getId() }));
		problems.add(I18n.getResource(LanguageKeys.ALARMS_ERROR_SETTING_NULL, false));
	    } else {

		// Recuperamos la alarma por defecto.
		Alarm defaultAlarm = setting.getDefaultAlarm();
		if (defaultAlarm == null) {
		    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_DEFAULT_ALARM_NULL, true,
			    new Object[] { user.getId() }));
		    problems.add(I18n.getResource(LanguageKeys.ALARMS_ERROR_SETTING_NULL, false));
		} else {

		    // Incluimos la alarma en el modelo
		    modelAndView.addObject(Parameters.DEFAULT_ALARM, defaultAlarm);

		    // añadimos la alarma por defecto a los atributos de sesión.
		    attributes.addFlashAttribute(Parameters.DEFAULT_ALARM, defaultAlarm);
		}
	    }
	    // Añadimos la lista de alertas.
	    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);

	    // Redirigimos la vista al formulario de creación de alarma.
	    modelAndView.setViewName(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
		    + PathResources.ALARMS + CommonsResources.SLASH + PathResources.CREATE_ALARM
		    + CommonsResources.SLASH + trackingId);
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

}
