package us.etsii.fvt.utils.schedules;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import us.etsii.fvt.domains.Alarm;
import us.etsii.fvt.domains.Notification;
import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.domains.Tracking;
import us.etsii.fvt.domains.Vulnerability;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.SettingService;
import us.etsii.fvt.services.TrackingService;
import us.etsii.fvt.services.VulnerabilityService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.NotificationUtils;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.enums.Period;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the scheduled methods set to check the software
 *         vulnerabilities from the alarm set.
 */
@Component
public class ScheduledAlarmTask {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(ScheduledAlarmTask.class);

    /**
     * vulnerability service attribute.
     */
    @Autowired
    private VulnerabilityService vulnerabilityService;

    /**
     * Notification service attribute.
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * Setting service attribute.
     */
    @Autowired
    private SettingService settingService;

    /**
     * Tracking service attribute.
     */
    @Autowired
    private TrackingService trackingService;

    /**
     * Scheduled method that checks if some notification must be created. This
     * scheduled method runs each 1 hour.
     */
    @Scheduled(initialDelayString = Parameters.INITIAL_DELAY_ONE_HOUR, fixedDelayString = Parameters.FIXED_DELAY_ONE_HOUR)
    public void checkVulnerabilities1() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_HOUR_START, true));

	    // Obtenemos todas los seguimientos de la base de datos.
	    List<Tracking> trackings = trackingService.findAll();
	    if (trackings == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.TRACKING_LIST_NULL, false));
	    }

	    // Recorremos las vulnerabilidades existentes en base de datos, y por cada
	    // descripción, comprobamos si el nombre del software de cada uno de los
	    // seguimientos está contenido en la descripción de alguna vulnerabilidad.
	    for (Vulnerability vul : vulnerabilityService.getAllVuls()) {
		for (Tracking tracking : trackings) {
		    if (vul.getDescription().toLowerCase().contains(tracking.getSoftwareName().toLowerCase())) {

			// Si coincide con la descripción de alguna vulnerabilidad, miramos si el
			// seguimiento tiene alarmas configuradas. Si es así, recuperamos el intervalo
			// temporal y comprobamos que sea igual al de la tarea programada.
			Set<Alarm> alarms = tracking.getAlarms();
			if (alarms != null && !alarms.isEmpty()) {
			    for (Alarm alarm : alarms) {
				if (alarm.getAlarmPeriod() == Period.ONE_HOUR.getValue() && alarm.isEnabled()) {
				    // Si la alarma tiene el mismo periodo que la tarea programada y está activada,
				    // creamos la notificación y la guardamos.
				    Notification not = new Notification();
				    not.setTracking(tracking);
				    not.setRead(false);
				    not.setUser(tracking.getUser());
				    not.setVulnerability(vul);
				    not.setCreationDate(LocalDateTime.now());
				    notificationService.saveNotification(not);

				    // Comprobamos si la alarma tiene activada la notificación por email,
				    // recuperamos la configuración del usuario y enviamos el correo.
				    if (alarm.isEmailNotification()) {
					Setting setting = settingService.findByUserId(tracking.getUser().getId());
					NotificationUtils.sendEmail(not, setting);
				    }
				}
			    }
			}
		    }
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_HOUR_END, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }
    
    /**
     * Scheduled method that checks if some notification must be created. This
     * scheduled method runs each 6 hours.
     */
    @Scheduled(initialDelayString = Parameters.INITIAL_DELAY_SIX_HOURS, fixedDelayString = Parameters.FIXED_DELAY_SIX_HOURS)
    public void checkVulnerabilities2() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_SIX_HOURS_START, true));

	    // Obtenemos todas los seguimientos de la base de datos.
	    List<Tracking> trackings = trackingService.findAll();
	    if (trackings == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.TRACKING_LIST_NULL, false));
	    }

	    // Recorremos las vulnerabilidades existentes en base de datos, y por cada
	    // descripción, comprobamos si el nombre del software de cada uno de los
	    // seguimientos está contenido en la descripción de alguna vulnerabilidad.
	    for (Vulnerability vul : vulnerabilityService.getAllVuls()) {
		for (Tracking tracking : trackings) {
		    if (vul.getDescription().toLowerCase().contains(tracking.getSoftwareName().toLowerCase())) {

			// Si coincide con la descripción de alguna vulnerabilidad, miramos si el
			// seguimiento tiene alarmas configuradas. Si es así, recuperamos el intervalo
			// temporal y comprobamos que sea igual al de la tarea programada.
			Set<Alarm> alarms = tracking.getAlarms();
			if (alarms != null && !alarms.isEmpty()) {
			    for (Alarm alarm : alarms) {
				if (alarm.getAlarmPeriod() == Period.SIX_HOURS.getValue() && alarm.isEnabled()) {
				    // Si la alarma tiene el mismo periodo que la tarea programada y está activada,
				    // creamos la notificación y la guardamos.
				    Notification not = new Notification();
				    not.setTracking(tracking);
				    not.setRead(false);
				    not.setUser(tracking.getUser());
				    not.setVulnerability(vul);
				    not.setCreationDate(LocalDateTime.now());
				    notificationService.saveNotification(not);

				    // Comprobamos si la alarma tiene activada la notificación por email,
				    // recuperamos la configuración del usuario y enviamos el correo.
				    if (alarm.isEmailNotification()) {
					Setting setting = settingService.findByUserId(tracking.getUser().getId());
					NotificationUtils.sendEmail(not, setting);
				    }
				}
			    }
			}
		    }
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_SIX_HOURS_END, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }
    
    /**
     * Scheduled method that checks if some notification must be created. This
     * scheduled method runs each 24 hours.
     */
    @Scheduled(initialDelayString = Parameters.INITIAL_DELAY_ONE_DAY, fixedDelayString = Parameters.FIXED_DELAY_ONE_DAY)
    public void checkVulnerabilities3() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_DAY_START, true));

	    // Obtenemos todas los seguimientos de la base de datos.
	    List<Tracking> trackings = trackingService.findAll();
	    if (trackings == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.TRACKING_LIST_NULL, false));
	    }

	    // Recorremos las vulnerabilidades existentes en base de datos, y por cada
	    // descripción, comprobamos si el nombre del software de cada uno de los
	    // seguimientos está contenido en la descripción de alguna vulnerabilidad.
	    for (Vulnerability vul : vulnerabilityService.getAllVuls()) {
		for (Tracking tracking : trackings) {
		    if (vul.getDescription().toLowerCase().contains(tracking.getSoftwareName().toLowerCase())) {

			// Si coincide con la descripción de alguna vulnerabilidad, miramos si el
			// seguimiento tiene alarmas configuradas. Si es así, recuperamos el intervalo
			// temporal y comprobamos que sea igual al de la tarea programada.
			Set<Alarm> alarms = tracking.getAlarms();
			if (alarms != null && !alarms.isEmpty()) {
			    for (Alarm alarm : alarms) {
				if (alarm.getAlarmPeriod() == Period.ONE_DAY.getValue() && alarm.isEnabled()) {
				    // Si la alarma tiene el mismo periodo que la tarea programada y está activada,
				    // creamos la notificación y la guardamos.
				    Notification not = new Notification();
				    not.setTracking(tracking);
				    not.setRead(false);
				    not.setUser(tracking.getUser());
				    not.setVulnerability(vul);
				    not.setCreationDate(LocalDateTime.now());
				    notificationService.saveNotification(not);

				    // Comprobamos si la alarma tiene activada la notificación por email,
				    // recuperamos la configuración del usuario y enviamos el correo.
				    if (alarm.isEmailNotification()) {
					Setting setting = settingService.findByUserId(tracking.getUser().getId());
					NotificationUtils.sendEmail(not, setting);
				    }
				}
			    }
			}
		    }
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_DAY_END, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }
    
    /**
     * Scheduled method that checks if some notification must be created. This
     * scheduled method runs each 7 days.
     */
    @Scheduled(initialDelayString = Parameters.INITIAL_DELAY_ONE_WEEK, fixedDelayString = Parameters.FIXED_DELAY_ONE_WEEK)
    public void checkVulnerabilities4() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_WEEK_START, true));

	    // Obtenemos todas los seguimientos de la base de datos.
	    List<Tracking> trackings = trackingService.findAll();
	    if (trackings == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.TRACKING_LIST_NULL, false));
	    }

	    // Recorremos las vulnerabilidades existentes en base de datos, y por cada
	    // descripción, comprobamos si el nombre del software de cada uno de los
	    // seguimientos está contenido en la descripción de alguna vulnerabilidad.
	    for (Vulnerability vul : vulnerabilityService.getAllVuls()) {
		for (Tracking tracking : trackings) {
		    if (vul.getDescription().toLowerCase().contains(tracking.getSoftwareName().toLowerCase())) {

			// Si coincide con la descripción de alguna vulnerabilidad, miramos si el
			// seguimiento tiene alarmas configuradas. Si es así, recuperamos el intervalo
			// temporal y comprobamos que sea igual al de la tarea programada.
			Set<Alarm> alarms = tracking.getAlarms();
			if (alarms != null && !alarms.isEmpty()) {
			    for (Alarm alarm : alarms) {
				if (alarm.getAlarmPeriod() == Period.ONE_WEEK.getValue() && alarm.isEnabled()) {
				    // Si la alarma tiene el mismo periodo que la tarea programada y está activada,
				    // creamos la notificación y la guardamos.
				    Notification not = new Notification();
				    not.setTracking(tracking);
				    not.setRead(false);
				    not.setUser(tracking.getUser());
				    not.setVulnerability(vul);
				    not.setCreationDate(LocalDateTime.now());
				    notificationService.saveNotification(not);

				    // Comprobamos si la alarma tiene activada la notificación por email,
				    // recuperamos la configuración del usuario y enviamos el correo.
				    if (alarm.isEmailNotification()) {
					Setting setting = settingService.findByUserId(tracking.getUser().getId());
					NotificationUtils.sendEmail(not, setting);
				    }
				}
			    }
			}
		    }
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_WEEK_END, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }
    
    /**
     * Scheduled method that checks if some notification must be created. This
     * scheduled method runs each 30 days.
     */
    @Scheduled(initialDelayString = Parameters.INITIAL_DELAY_ONE_MONTH, fixedDelayString = Parameters.FIXED_DELAY_ONE_MONTH)
    public void checkVulnerabilities5() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_MONTH_START, true));

	    // Obtenemos todas los seguimientos de la base de datos.
	    List<Tracking> trackings = trackingService.findAll();
	    if (trackings == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.TRACKING_LIST_NULL, false));
	    }

	    // Recorremos las vulnerabilidades existentes en base de datos, y por cada
	    // descripción, comprobamos si el nombre del software de cada uno de los
	    // seguimientos está contenido en la descripción de alguna vulnerabilidad.
	    for (Vulnerability vul : vulnerabilityService.getAllVuls()) {
		for (Tracking tracking : trackings) {
		    if (vul.getDescription().toLowerCase().contains(tracking.getSoftwareName().toLowerCase())) {

			// Si coincide con la descripción de alguna vulnerabilidad, miramos si el
			// seguimiento tiene alarmas configuradas. Si es así, recuperamos el intervalo
			// temporal y comprobamos que sea igual al de la tarea programada.
			Set<Alarm> alarms = tracking.getAlarms();
			if (alarms != null && !alarms.isEmpty()) {
			    for (Alarm alarm : alarms) {
				if (alarm.getAlarmPeriod() == Period.ONE_MONTH.getValue() && alarm.isEnabled()) {
				    // Si la alarma tiene el mismo periodo que la tarea programada y está activada,
				    // creamos la notificación y la guardamos.
				    Notification not = new Notification();
				    not.setTracking(tracking);
				    not.setRead(false);
				    not.setUser(tracking.getUser());
				    not.setVulnerability(vul);
				    not.setCreationDate(LocalDateTime.now());
				    notificationService.saveNotification(not);

				    // Comprobamos si la alarma tiene activada la notificación por email,
				    // recuperamos la configuración del usuario y enviamos el correo.
				    if (alarm.isEmailNotification()) {
					Setting setting = settingService.findByUserId(tracking.getUser().getId());
					NotificationUtils.sendEmail(not, setting);
				    }
				}
			    }
			}
		    }
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_ONE_MONTH_END, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }
    
    /**
     * Scheduled method that checks if some notification must be created. This
     * scheduled method runs each 6 months.
     */
    @Scheduled(initialDelayString = Parameters.INITIAL_DELAY_SIX_MONTHS, fixedDelayString = Parameters.FIXED_DELAY_SIX_MONTHS)
    public void checkVulnerabilities6() {
	try {
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_SIX_MONTHS_START, true));

	    // Obtenemos todas los seguimientos de la base de datos.
	    List<Tracking> trackings = trackingService.findAll();
	    if (trackings == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.TRACKING_LIST_NULL, false));
	    }

	    // Recorremos las vulnerabilidades existentes en base de datos, y por cada
	    // descripción, comprobamos si el nombre del software de cada uno de los
	    // seguimientos está contenido en la descripción de alguna vulnerabilidad.
	    for (Vulnerability vul : vulnerabilityService.getAllVuls()) {
		for (Tracking tracking : trackings) {
		    if (vul.getDescription().toLowerCase().contains(tracking.getSoftwareName().toLowerCase())) {

			// Si coincide con la descripción de alguna vulnerabilidad, miramos si el
			// seguimiento tiene alarmas configuradas. Si es así, recuperamos el intervalo
			// temporal y comprobamos que sea igual al de la tarea programada.
			Set<Alarm> alarms = tracking.getAlarms();
			if (alarms != null && !alarms.isEmpty()) {
			    for (Alarm alarm : alarms) {
				if (alarm.getAlarmPeriod() == Period.SIX_MONTHS.getValue() && alarm.isEnabled()) {
				    // Si la alarma tiene el mismo periodo que la tarea programada y está activada,
				    // creamos la notificación y la guardamos.
				    Notification not = new Notification();
				    not.setTracking(tracking);
				    not.setRead(false);
				    not.setUser(tracking.getUser());
				    not.setVulnerability(vul);
				    not.setCreationDate(LocalDateTime.now());
				    notificationService.saveNotification(not);

				    // Comprobamos si la alarma tiene activada la notificación por email,
				    // recuperamos la configuración del usuario y enviamos el correo.
				    if (alarm.isEmailNotification()) {
					Setting setting = settingService.findByUserId(tracking.getUser().getId());
					NotificationUtils.sendEmail(not, setting);
				    }
				}
			    }
			}
		    }
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CHECK_VULS_SCHEDULED_SIX_MONTHS_END, true));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

}
