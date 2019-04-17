package us.etsii.fvt.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.etsii.fvt.domains.Notification;
import us.etsii.fvt.repositories.NotificationRepository;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         TThis class represents the notification service.
 */
@Service
@Transactional
public class NotificationService {

    /**
     * Notification repository attribute.
     */
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(NotificationService.class);

    /**
     * Method that saves a notification.
     * 
     * @param notification Object to save in the database.
     */
    public void saveNotification(Notification notification) {
	try {
	    if (notification == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_NOTIFICATION_NULL, true));
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_SAVE_NOTIFICATION, true,
			new Object[] { notification.getVulnerability().getName() }));
		Notification savedNotification = notificationRepository.save(notification);
		if (savedNotification.getId() != null) {
		    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_SAVE_NOTIFICATION_SUCCESSFULLY,
			    true, new Object[] { savedNotification.getVulnerability().getName(),
				    savedNotification.getId() }));
		} else {
		    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_SAVE_NOTIFICATION_FAILURE, true,
			    new Object[] { notification.getVulnerability().getName() }));
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that finds a notification by its identifier.
     * 
     * @param id Identifier of the notification to search.
     * @return a notification object whose id matches with the given one, or null if
     *         there is not exists.
     */
    public Notification findById(String id) {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_FIND_ID, true, new Object[] { id }));
	    Optional<Notification> n = notificationRepository.findById(id);
	    if (!n.isPresent()) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_NOTIFICATION_NOT_FOUND, true,
			new Object[] { id }));
		return null;
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_NOTIFICATION_FOUND, true,
		    new Object[] { id, n.get().getVulnerability().getName() }));
	    return n.get();
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_NOTIFICATION_ID_NULL, true,
		    new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that searches every notification by the user identifier.
     * 
     * @param userId Identifier of the user.
     * @return a list of notifications whose user identifiers match with the given
     *         ones.
     */
    public List<Notification> findAllByUser(String userId) {
	try {
	    if (userId == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_USER_ID_NULL, true));
		return null;
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL_ID, true, new Object[] { userId }));
	    List<Notification> n = notificationRepository.findAllByUser(userId);
	    if (n == null || n.isEmpty()) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL_EMPTY_LIST, true,
			new Object[] { userId }));
		return null;
	    } else {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_FIND_ALL_SUCESSFULLY, true,
			new Object[] { userId, n.size() }));
		return n;
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that gets a set of notifications according to the page requested.
     * 
     * @param pageable Parameter that indicates the page requested.
     * @param userId   User identifier.
     * @return a new page of the notifications list.
     */
    public Page<Notification> findAllNotificationsPaginated(Pageable pageable, String userId) {
	try {
	    // Recuperamos los parámetros de paginación.
	    int pageSize = pageable.getPageSize();
	    int currentPage = pageable.getPageNumber();
	    int startItem = currentPage * pageSize;

	    // iniciamos la lista que contendrá la sublista de notificaciones a mostrar.
	    List<Notification> notsPageList;

	    // Recuperamos todas las notificaciones del usuario
	    List<Notification> nots = findAllByUser(userId);

	    // Si no hay notificaciones que mostrar, devolvemos null.
	    if (nots == null) {
		LOGGER.debug(
			I18n.getResource(LanguageKeys.LogsMessages.LOG_NO_NOTIFICATION, true, new Object[] { userId }));
		return null;
	    }

	    // Si el número de notificaciones es menor al número solicitado, devolvemos la
	    // lista vacía, sino, devolvemos las notificaciones disponibles hasta
	    // completar el número solicitado.
	    if (nots.size() < startItem) {
		notsPageList = Collections.emptyList();
	    } else {
		int toIndex = Math.min(startItem + pageSize, nots.size());
		notsPageList = nots.subList(startItem, toIndex);
	    }

	    // Recuperamos la página a mostrar y la devolvemos.
	    Page<Notification> notsPage = new PageImpl<Notification>(notsPageList,
		    PageRequest.of(currentPage, pageSize), nots.size());
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_NOTS_ALL_FOUND, true,
		    new Object[] { notsPage.getNumberOfElements(), notsPage.getTotalElements() }));
	    return notsPage;

	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that removes a notification by its identifier.
     * 
     * @param id Identifier of the notification to remove.
     */
    public void remove(String id) {
	try {
	    LOGGER.debug(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_NOTIFICATION, true, new Object[] { id }));
	    notificationRepository.deleteById(id);
	    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_REMOVE_NOTIFICATION_SUCCESSFULLY, true,
		    new Object[] { id }));
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_SERVICE_ERROR_ID_NULL, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that indicates if there exists unread notification for the user.
     * 
     * @param userId User identifier.
     * @return True if there are notifications unread, False in other case.
     */
    public boolean userHasNotifications(String userId) {
	try {
	    // Recuperamos todas las notificaciones del usuario que tengan el campo 'read' a
	    // false.
	    List<Notification> nots = notificationRepository.getAllUnreadNotificationByUser(userId);
	    // Si la lista no es nula ni esta vacía, devolvemos true.
	    return nots != null && !nots.isEmpty();
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * Method that change the read status of a notification from not read to read.
     * 
     * @param notifications Notifications list to set.
     */
    public void notificationRead(List<Notification> notifications) {
	try {
	    // Recorremos la lista de notificaciones. Por cada una de ellas, cambiamos su
	    // estado de no leido a leido y la guardamos en base de datos.
	    for (Notification not : notifications) {
		not.setRead(true);
		notificationRepository.save(not);
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that removes all notification related with a tracking.
     * 
     * @param id Tracking identifier.
     */
    public void removeAllByTracking(String id) {
	try {
	    // Recuperamos la lista de notificaciones relacionadas con el seguimiento dado.
	    List<Notification> nots = notificationRepository.findAllByTracking(id);
	    if (nots != null) {
		// Eliminamos cada notificación encontrada.
		for (Notification not : nots) {
		    remove(not.getId());
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Method that removes all notification of a given user.
     * 
     * @param id User identifier.
     */
    public void removeAll(String id) {
	try {
	    // Recuperamos la lista de notificaciones relacionadas con el usuario dado.
	    List<Notification> nots = notificationRepository.findAllByUser(id);
	    if (nots != null) {
		// Eliminamos todas las notificaciones encontradas.
		for (Notification not : nots) {
		    remove(not.getId());
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}

    }

}
