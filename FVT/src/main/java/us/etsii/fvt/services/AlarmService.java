package us.etsii.fvt.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.etsii.fvt.domains.Alarm;
import us.etsii.fvt.repositories.AlarmRepository;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the alarm service.
 */
@Service
@Transactional
public class AlarmService {

    /**
     * Alarm repository attribute.
     */
    @Autowired
    private AlarmRepository alarmRepository;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(AlarmService.class);

    /**
     * Method that finds an alarm by its identifier.
     * 
     * @param alarmId Alarm identifier.
     * @return an alarm object if there exists an object whose ID matches with the
     *         given one, or null in other case.
     */
    public Alarm findById(String alarmId) {
	try {
	    LOGGER.debug(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALARM_BY_ID, true, new Object[] { alarmId }));
	    Optional<Alarm> opt = alarmRepository.findById(alarmId);
	    if (!opt.isPresent()) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_NOT_FOUND, true,
			new Object[] { alarmId }));
		return null;
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_FOUND, true,
		    new Object[] { alarmId, opt.get().getAlarmName() }));
	    return opt.get();
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_ID_NULL, true, new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that saves an alarm.
     * 
     * @param alarm Alarm to save in the database.
     * @return True if there are no exceptions in the process and False in other
     *         case.
     */
    public boolean saveAlarm(Alarm alarm) {
	try {
	    if (alarm != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SAVE_ALARM, true,
			new Object[] { alarm.getAlarmName() }));
		alarmRepository.save(alarm);
		return true;
	    } else {
		throw new IllegalArgumentException();
	    }
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SAVE_ALARM_NULL, true));
	    return false;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * Method that removes an alarm from database.
     * 
     * @param id Alarm identifier.
     * @return True if the process has been finished without exceptions,False in
     *         other case.
     */
    public boolean remove(String id) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_ALARM, true, new Object[] { id }));
	    alarmRepository.deleteById(id);
	    return true;
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_ID_NULL, true, new Object[] { e }));
	    return false;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * Method that edits an alarm from database.
     * 
     * @param id            Alarm identifier.
     * @param alarm         New alarm object to save.
     * @param updateEnabled Indicates if the enabled attribute should be update too.
     * @return True if the process has finished successfully, False in other case.
     */
    public boolean editAlarm(String id, @Valid Alarm alarm, boolean updateEnabled) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EDIT_ALARM_START, true, new Object[] { id }));
	    Alarm currentAlarm = findById(id);
	    // Si ambas alarmas, la actual y la nueva, no son nulas, continuamos
	    if (currentAlarm != null && alarm != null) {
		boolean needUpdate = false;
		// Comprobamos cada uno de los campos que han podido ser modificados. Si se ha
		// cambiado el valor de algún campo, se actualizará.
		if (!currentAlarm.getAlarmName().equals(alarm.getAlarmName())) {
		    currentAlarm.setAlarmName(alarm.getAlarmName());
		    needUpdate = true;
		}
		if (updateEnabled && currentAlarm.isEnabled() != alarm.isEnabled()) {
		    currentAlarm.setEnabled(alarm.isEnabled());
		    needUpdate = true;
		}
		if (currentAlarm.isEmailNotification() != alarm.isEmailNotification()) {
		    currentAlarm.setEmailNotification(alarm.isEmailNotification());
		    needUpdate = true;
		}
		if (currentAlarm.getAlarmPeriod() != alarm.getAlarmPeriod()) {
		    currentAlarm.setAlarmPeriod(alarm.getAlarmPeriod());
		    needUpdate = true;
		}
		// Si es necesario, realizamos la actualización
		if (needUpdate) {
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EDIT_ALARM_FINISH, true));
		    saveAlarm(currentAlarm);
		} else {
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EDIT_ALARM_FINISH_2, true));
		}
		return true;
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_EDIT_ALARM_NULL, true));
		return false;
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * Method that removes a set of alarms from database.
     * 
     * @param alarms Set of alarm to remove.
     */
    public void removeAll(Set<Alarm> alarms) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_REMOVE_ALL, true));
	    alarmRepository.deleteAll(alarms);
	} catch (IllegalArgumentException e) {
	    LOGGER.error(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_SET_NULL, true, new Object[] { e }));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that updates an alarm with another new one.
     * 
     * @param id    Alarm identifier.
     * @param alarm New alarm to replace.
     * @return True if the task has been finished successfully, False in other case.
     */
    public boolean UpdateAlarm(String id, Alarm alarm) {
	try {
	    // Comprobamos que ni el ID ni la alarma sean nulos.
	    LOGGER.debug(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_UPDATE, true, new Object[] { id, alarm }));
	    if (id == null || alarm == null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_ID_OR_ALARM_NULL, true,
			new Object[] { id, alarm }));
		return false;
	    }
	    // Recuperamos la alarma a reemplazar de la base de datos.
	    Alarm a = findById(id);
	    if (a == null) {
		return false;
	    }
	    // Comparamos cada campo y actualizamos en caso de ser necesario.
	    a.setAlarmName(!a.getAlarmName().equals(alarm.getAlarmName()) ? alarm.getAlarmName() : a.getAlarmName());
	    a.setAlarmPeriod(
		    a.getAlarmPeriod() != alarm.getAlarmPeriod() ? alarm.getAlarmPeriod() : a.getAlarmPeriod());
	    a.setEmailNotification(a.isEmailNotification() != alarm.isEmailNotification() ? alarm.isEmailNotification()
		    : a.isEmailNotification());
	    a.setEnabled(a.isEnabled() != alarm.isEnabled() ? alarm.isEnabled() : a.isEnabled());

	    // Guardamos la nueva alarma
	    boolean saveSuccess = saveAlarm(a);
	    if (saveSuccess) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_UPDATE_SUCESSFULLY, true,
			new Object[] { a.getId() }));
		return true;
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_ALARM_SAVE_FAILURE, true,
			new Object[] { id }));
		return false;
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * 
     * Method that find the alarm page required by the user.
     * 
     * @param pageable Parameter that indicates the page requested.
     * @param alarmSet Set of alarm to paginate.
     * @return a new page of alarms.
     */
    public Page<Alarm> findAllAlarmsPaginated(PageRequest pageable, Set<Alarm> alarmSet) {
	try {
	    // Recuperamos los parámetros de paginación.
	    int pageSize = pageable.getPageSize();
	    int currentPage = pageable.getPageNumber();
	    int startItem = currentPage * pageSize;

	    // iniciamos la lista que contendrá la sublista de alarmas a mostrar.
	    List<Alarm> alarmPageList;

	    // Cargamos la lista completa de alarmas.
	    List<Alarm> alarms = new ArrayList<Alarm>();
	    alarms.addAll(alarmSet);

	    // Si el número de alarmas es menor al número solicitado, devolvemos la
	    // lista vacía, sino, devolvemos las alarmas disponibles hasta
	    // completar el número solicitado.
	    if (alarms.size() < startItem) {
		alarmPageList = Collections.emptyList();
	    } else {
		int toIndex = Math.min(startItem + pageSize, alarms.size());
		alarmPageList = alarms.subList(startItem, toIndex);
	    }

	    // Recuperamos la página a mostrar y la devolvemos.
	    Page<Alarm> alarmPage = new PageImpl<Alarm>(alarmPageList, PageRequest.of(currentPage, pageSize),
		    alarms.size());
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ALARMS_ALL_FOUND, true,
		    new Object[] { alarmPage.getNumberOfElements(), alarmPage.getTotalElements() }));
	    return alarmPage;

	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

}
