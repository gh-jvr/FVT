package us.etsii.fvt.services;

import java.util.Collections;
import java.util.HashSet;
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
import us.etsii.fvt.domains.Tracking;
import us.etsii.fvt.repositories.TrackingRepository;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the tracking service.
 */
@Service
@Transactional
public class TrackingService {

    /**
     * tracking repository attribute.
     */
    @Autowired
    private TrackingRepository trackingRepository;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(TrackingService.class);

    /**
     * Method that saves a tracking.
     * 
     * @param tracking Object to save in the database.
     */
    public void saveTracking(Tracking tracking) {
	try {
	    if (tracking == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_TRACKING_NULL, true));
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_SAVE_TRACKING, true,
			new Object[] { tracking.getTrackingName() }));
		Tracking savedTracking = trackingRepository.save(tracking);
		if (savedTracking.getId() != null) {
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_SAVE_TRACKING_SUCCESSFULLY, true,
			    new Object[] { savedTracking.getTrackingName(), savedTracking.getId() }));
		} else {
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_SAVE_TRACKING_FAILURE, true,
			    new Object[] { tracking.getTrackingName() }));
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that finds a tracking by its identifier.
     * 
     * @param id Identifier of the tracking to search.
     * @return a tracking object whose id matches with the given one, or null if
     *         there is not exists.
     */
    public Tracking findById(String id) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_FIND_ID, true, new Object[] { id }));
	    Optional<Tracking> t = trackingRepository.findById(id);
	    if (!t.isPresent()) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_TRACKING_NOT_FOUND, true,
			new Object[] { id }));
		return null;
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_TRACKING_FOUND, true,
		    new Object[] { id, t.get().getTrackingName() }));
	    return t.get();
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_TRACKING_ID_NULL, true,
		    new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that gets all tracking object from database.
     * 
     * @return a list with every tracking object from database or null in error
     *         case.
     */
    public List<Tracking> findAll() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL, true));
	    List<Tracking> trackings = trackingRepository.findAll();
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_TRACKING_ALL_FOUND, true,
		    new Object[] { trackings.size() }));
	    return trackings;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that searches every tracking by the user identifier.
     * 
     * @param userId Identifier of the user.
     * @return a list of trackings whose user identifiers match with the given ones.
     */
    public List<Tracking> findAllByUser(String userId) {
	try {
	    if (userId == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_ID_NULL, true));
		return null;
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL_ID, true, new Object[] { userId }));
	    List<Tracking> t = trackingRepository.findAllByUser(userId);
	    if (t == null || t.isEmpty()) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL_EMPTY_LIST, true,
			new Object[] { userId }));
		return null;
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL_SUCESSFULLY, true,
			new Object[] { userId, t.size() }));
		return t;
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that edit a tracking in database.
     * 
     * @param trackingId Identifier of the tracking.
     * @param tracking   New tracking to replace.
     */
    public void editTracking(String trackingId, Tracking tracking) {
	try {
	    // Recuperamos el seguimiento antes de la modificación
	    Tracking oldTracking = findById(trackingId);
	    boolean saveNew = false;
	    // Si ambos seguimientos (el viejo y el nuevo) son distintos de nulo, seguimos
	    // realizando comprobaciones.
	    if (tracking != null && oldTracking != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EDIT_TRACKING, true,
			new Object[] { oldTracking.getId() }));
		// Si el nombre del seguimiento ha sido cambiado, actualizamos el seguimiento
		// antiguo.
		if (tracking.getTrackingName() != null
			&& !tracking.getTrackingName().equals(oldTracking.getTrackingName())) {
		    oldTracking.setTrackingName(tracking.getTrackingName());
		    saveNew = true;
		}
		// Si el nombre del software ha sido modificado, actualizamos el seguimiento
		// antiguo.
		if (tracking.getSoftwareName() != null
			&& !tracking.getSoftwareName().equals(oldTracking.getSoftwareName())) {
		    oldTracking.setSoftwareName(tracking.getSoftwareName());
		    saveNew = true;
		}
		// Si el conjunto de alarmas ha sido modificado, actualizamos el seguimiento
		// antiguo.
		if (tracking.getAlarms() != null && !tracking.getAlarms().equals(oldTracking.getAlarms())) {
		    oldTracking.setAlarms(tracking.getAlarms());
		    saveNew = true;
		}
		// Si ha sido necesario realizar alguna actualización con respecto al
		// seguimiento antiguo, guardamos los cambios.
		if (saveNew) {
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EDIT_TRACKING_NECESSARY, true,
			    new Object[] { oldTracking.getId() }));
		    saveTracking(oldTracking);
		} else {
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EDIT_TRACKING_NOT_NECESSARY, true,
			    new Object[] { oldTracking.getId() }));
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_PARAMETERS_NULL, true,
			new Object[] { tracking, oldTracking }));
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that removes a tracking by its identifier.
     * 
     * @param id Identifier of the tracking to remove.
     */
    public void remove(String id) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_TRACKING, true, new Object[] { id }));
	    trackingRepository.deleteById(id);
	    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_TRACKING_SUCCESSFULLY, true,
		    new Object[] { id }));
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_ID_NULL, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that adds a new alarm to the alarms set.
     * 
     * @param id    Tracking identifier.
     * @param alarm New alarm object to add.
     * @return True if the process has finished correctly and False in other case.
     */
    public boolean AddAlarm(String id, @Valid Alarm alarm) {
	try {
	    if (alarm == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_TRACKING_NOT_FOUND, true,
			new Object[] { id }));
		return false;
	    }
	    // Recuperamos el seguimiento de la base de datos.
	    Tracking tracking = findById(id);
	    // Si existe el seguimiento, continuamos.
	    if (tracking != null) {
		Set<Alarm> alarms = tracking.getAlarms();
		// Si el conjunto de alarmas es nulo o no contiene la alarma que intentamos
		// añadir, la añadimos y actualizamos el seguimiento.
		if (alarms != null) {
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ALARMS_OBTAINED, true,
			    new Object[] { alarms.size(), id }));
		    if (!alarms.contains(alarm)) {
			alarms.add(alarm);
			saveTracking(tracking);
			LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ALARM_RELATED_SUCCESSFULLY,
				true, new Object[] { alarm.getId(), id }));
			return true;
		    } else {
			LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ALARM_ALREADY_EXISTS, true,
				new Object[] { alarm.getId() }));
			return false;
		    }
		} else {
		    alarms = new HashSet<Alarm>();
		    alarms.add(alarm);
		    saveTracking(tracking);
		    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ALARM_RELATED_SUCCESSFULLY,
			    true, new Object[] { alarm.getId(), id }));
		    return true;
		}
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_TRACKING_NOT_FOUND, true,
			new Object[] { id }));
		return false;
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * Method that find the tracking page required by the user.
     * 
     * @param pageable Parameter that indicates the page requested.
     * @param userId   User identifier.
     * @return a new page of trackings.
     */
    public Page<Tracking> findAllTrackingsPaginated(PageRequest pageable, String userId) {
	try {
	    // Recuperamos los parámetros de paginación.
	    int pageSize = pageable.getPageSize();
	    int currentPage = pageable.getPageNumber();
	    int startItem = currentPage * pageSize;

	    // iniciamos la lista que contendrá la sublista de seguimientos a mostrar.
	    List<Tracking> trackingPageList;

	    // Cargamos la lista completa de seguimientos
	    List<Tracking> trackings = findAllByUser(userId);

	    // Si el número de seguimientos es menor al número solicitado, devolvemos la
	    // lista vacía, sino, devolvemos los seguimientos disponibles hasta
	    // completar el número solicitado.
	    if (trackings.size() < startItem) {
		trackingPageList = Collections.emptyList();
	    } else {
		int toIndex = Math.min(startItem + pageSize, trackings.size());
		trackingPageList = trackings.subList(startItem, toIndex);
	    }

	    // Recuperamos la página a mostrar y la devolvemos.
	    Page<Tracking> trackingPage = new PageImpl<Tracking>(trackingPageList,
		    PageRequest.of(currentPage, pageSize), trackings.size());
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_TRACKINGS_ALL_FOUND, true,
		    new Object[] { trackingPage.getNumberOfElements(), trackingPage.getTotalElements() }));
	    return trackingPage;

	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }
}
