package us.etsii.fvt.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import us.etsii.fvt.domains.Alarm;
import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.services.AlarmService;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.SettingService;
import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.Validations;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;
import us.etsii.fvt.utils.enums.Period;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the settings controller.
 */
@SessionAttributes({ Parameters.INFOS, Parameters.PROBLEMS })
@Controller
public class SettingController {

    /**
     * User service attribute.
     */
    @Autowired
    private UserService userService;

    /**
     * Setting service attribute.
     */
    @Autowired
    private SettingService settingService;

    /**
     * Alarm service attribute.
     */
    @Autowired
    private AlarmService alarmService;

    /**
     * Notification service attribute.
     */
    @Autowired
    private NotificationService notificationService;

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
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(SettingController.class);

    /**
     * Method that maps the request for the resource '/setting' through of a get
     * HTTP method.
     * 
     * @param infos         List of information messages to show.
     * @param problems      List of error messages to show.
     * @param sessionStatus Indicates the status of the session.
     * @return a new view with the settings form.
     */
    @RequestMapping(value = { CommonsResources.SLASH + PathResources.SETTING }, method = RequestMethod.GET)
    public ModelAndView setting(@ModelAttribute(Parameters.INFOS) List<String> infos,
	    @ModelAttribute(Parameters.PROBLEMS) List<String> problems, SessionStatus sessionStatus) {
	try {
	    ModelAndView modelAndView = new ModelAndView();
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());
		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_RESOURCES_REQUEST, true,
			new Object[] { PathResources.SETTING, user.getId() }));
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

		// Recuperamos la configuración del usuario
		Setting setting = settingService.findByUserId(user.getId());
		if (setting != null) {
		    modelAndView.addObject(Parameters.SETTING, setting);
		    if (setting.getDefaultAlarm() != null) {
			modelAndView.addObject(Parameters.DEFAULT_ALARM, setting.getDefaultAlarm());
		    } else {
			modelAndView.addObject(Parameters.DEFAULT_ALARM, new Alarm());
		    }
		} else {
		    modelAndView.addObject(Parameters.SETTING, new Setting());
		    modelAndView.addObject(Parameters.DEFAULT_ALARM, new Alarm());
		}

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

		// Si es necesario, eliminamos las variables de la sesión.
		if (setComplete) {
		    sessionStatus.setComplete();
		}

		// Recuperamos la lista de periodos y la añadimos al modelo.
		List<Integer> periods = Period.getAllValues();
		modelAndView.addObject(Parameters.PERDIODS, periods);

		modelAndView.setViewName(PathResources.SETTING);
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
     * Method that maps the request for the resource '/saveTracking' through of a
     * post HTTP method and save the form in the database.
     * 
     * @param setting    New setting object.
     * @param alarm      Default alarm to save.
     * @param errors     Binding object to return errors to form.
     * @param attributes Parameter that stores attributes into the user session.
     * @return a redirected view of the user settings.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.SAVE_SETTING, method = RequestMethod.POST)
    public ModelAndView saveSetting(@Valid Setting setting, Alarm alarm, BindingResult errors,
	    RedirectAttributes attributes) {
	try {
	    // iniciamos las variables para mostrar los mensajes de alertas.
	    List<String> infos = new ArrayList<String>();
	    List<String> problems = new ArrayList<String>();
	    if (errors.hasErrors()) {
		attributes.addFlashAttribute(Parameters.ERRORS, errors.getAllErrors());
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.SETTING);
	    }
	    // Recuperamos el usuario.
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Si tenemos acceso a la información de autenticación...
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());

		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}

		// Si la configuración es nulo. Lanzamos error.
		if (setting == null) {
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_NULL, true));
		    problems.add(I18n.getResource(LanguageKeys.SETTING_NOT_SAVED, false));
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    return new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
			    + PathResources.SETTING);
		}

		// Validamos los campos.
		List<String> validationErrorMessages = Validations.validateSetting(setting);

		// Si los campos de la configuración no son válidos, lanzamos los errores.
		if (!validationErrorMessages.isEmpty()) {
		    problems.addAll(validationErrorMessages);
		    attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		    return new ModelAndView(Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH
			    + PathResources.SETTING);
		}
		// Buscamos en la base de datos si ya existe una configuración previa.
		Setting settingExists = settingService.findByUserId(user.getId());
		if (settingExists != null) {
		    // Si el setting de la base de datos no es nulo...

		    // Comprobamos si tiene configurada una alarma y si hay que actualizarla.
		    if (alarm != null) {

			// Comprobamos que sea una alarma válida.
			boolean validAlarm = Validations.checkAlarm(alarm);
			if (validAlarm) {

			    // Si la alarma es válida, la actualizamos.
			    boolean savedCorrectly = false;

			    if (settingExists.getDefaultAlarm() != null) {
				// Si ya existía una alarma previamente, la actualizamos.
				savedCorrectly = alarmService.UpdateAlarm(settingExists.getDefaultAlarm().getId(),
					alarm);
				Alarm a = alarmService.findById(settingExists.getDefaultAlarm().getId());
				setting.setDefaultAlarm(savedCorrectly ? a : null);

			    } else {
				// Si no existía ninguna alarma previamente, la creamos.
				savedCorrectly = alarmService.saveAlarm(alarm);
				setting.setDefaultAlarm(savedCorrectly ? alarm : null);
			    }

			    if (!savedCorrectly) {
				LOGGER.error(
					I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_ALARM_NOT_SAVED,
						true, new Object[] { alarm.getAlarmName(), settingExists.getId() }));
				problems.add(I18n.getResource(LanguageKeys.SETTING_ALARM_NOT_SAVED, false));
			    }

			} else {
			    // Si la alarma no es válida, sustituimos la nueva alarma por la vieja, de forma
			    // que se mantiene la alarma válida.
			    setting.setDefaultAlarm(settingExists.getDefaultAlarm());
			    // Mostramos mensaje de que no se ha guardado la alarma porque no es válida.
			    infos.add(I18n.getResource(LanguageKeys.ALARM_NOT_SAVE_BECAUSE_NOT_VALID, false));
			}
		    }
		    boolean updateSuccess = settingService.updateSetting(settingExists.getId(), setting);
		    if (updateSuccess) {
			LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_SAVED_SUCCESSFULLY, true));
			infos.add(I18n.getResource(LanguageKeys.SETTING_SAVED_SUCCESSFULLY, false));

		    } else {
			LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_FAILURE_UPDATE, true,
				new Object[] { alarm.getAlarmName(), settingExists.getId() }));
			problems.add(I18n.getResource(LanguageKeys.SETTING_NOT_SAVED, false));
		    }
		} else {
		    // Creamos uno nuevo...
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_CREATE_NEW_SETTING, true,
			    new Object[] { user.getId() }));
		    // Si se han introducido datos de la alarma, intentamos guardarla.
		    if (alarm != null) {
			// Comprobamos que sea una alarma válida.
			boolean validAlarm = Validations.checkAlarm(alarm);
			if (validAlarm) {
			    // Si la alarma es válida, la guardamos.
			    boolean savedCorrectly = alarmService.saveAlarm(alarm);
			    // Actualizamos la configuración.
			    setting.setDefaultAlarm(savedCorrectly ? alarm : null);
			}
		    }
		    setting.setUser(user);
		    boolean savedCorrectly = settingService.saveSetting(setting);
		    if (!savedCorrectly) {
			LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_NOT_SAVED, true));
			problems.add(I18n.getResource(LanguageKeys.SETTING_NOT_SAVED, false));
		    } else {
			LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_SAVED_SUCCESSFULLY, true));
			infos.add(I18n.getResource(LanguageKeys.SETTING_SAVED_SUCCESSFULLY, false));
		    }
		}
		attributes.addFlashAttribute(Parameters.INFOS, infos);
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.SETTING);
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
     * Method that maps the request for the resource '/removeDefaultAlarm' through
     * of a post HTTP method and save the form in the database.
     * 
     * @param attributes Parameter that stores attributes into the user session.
     * @return a redirected view of the user settings.
     */
    @RequestMapping(value = CommonsResources.SLASH + PathResources.REMOVE_DEFAULT_ALARM, method = RequestMethod.POST)
    public ModelAndView removeSetting(RedirectAttributes attributes) {
	try {
	    // iniciamos las variables que vamos a necesitar a lo largo del método
	    List<String> infos = new ArrayList<String>();
	    List<String> problems = new ArrayList<String>();

	    // Recuperamos el usuario.
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Si tenemos acceso a la información de autenticación...
	    if (auth != null) {
		User user = userService.findUserByEmail(auth.getName());

		// Si el usuario no existe en base de datos, lanzamos una excepción
		if (user == null) {
		    throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_USER_NOT_EXISTS, false));
		}

		// Recuperamos la configuración
		Setting setting = settingService.findByUserId(user.getId());

		// Comprobamos que la alarma no sea nula y exista en la base de datos.
		if (setting != null && setting.getDefaultAlarm() != null && setting.getDefaultAlarm().getId() != null
			&& alarmService.findById(setting.getDefaultAlarm().getId()) != null) {

		    // Eliminamos la alarma de la base de datos
		    alarmService.remove(setting.getDefaultAlarm().getId());
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_DEFAULT_ALARM_REMOVED, true,
			    new Object[] { user.getId() }));

		    // Actualizamos la configuración
		    setting.setDefaultAlarm(null);
		    settingService.updateSetting(setting.getId(), setting);

		    // mostramos mensajes de confirmación.
		    infos.add(I18n.getResource(LanguageKeys.REMOVE_DEFAULT_ALARM_SUCCESS, false));

		} else {
		    // Mostramos mensajes de error.
		    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_NOT_VALID, true));
		    problems.add(I18n.getResource(LanguageKeys.REMOVE_DEFAULT_ALARM_ERROR, false));
		}
		// Añadimos las variables de alertas a la sesión y devolvemos el modelo.
		attributes.addFlashAttribute(Parameters.INFOS, infos);
		attributes.addFlashAttribute(Parameters.PROBLEMS, problems);
		return new ModelAndView(
			Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.SETTING);
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
