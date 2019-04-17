package us.etsii.fvt.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import us.etsii.fvt.domains.Dashboard;
import us.etsii.fvt.domains.Notification;
import us.etsii.fvt.domains.Tracking;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.domains.Vulnerability;
import us.etsii.fvt.services.DashboardService;
import us.etsii.fvt.services.NotificationService;
import us.etsii.fvt.services.TrackingService;
import us.etsii.fvt.services.UserService;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.PathResources;
import us.etsii.fvt.utils.enums.Kpi;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the dashboard controller.
 */
@Controller
public class DashboardController {

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
     * tracking service attribute.
     */
    @Autowired
    private TrackingService trackingService;

    /**
     * dashboard service attribute.
     */
    @Autowired
    private DashboardService dashboardService;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(DashboardController.class);

    /**
     * Method that maps the request for the resource '/dashboard' through of a get
     * HTTP method.
     * 
     * @return a new view with the dashboard data.
     */
    @SuppressWarnings(CommonsResources.UNCHECKED)
    @RequestMapping(value = CommonsResources.SLASH + PathResources.DASHBOARD, method = RequestMethod.GET)
    public ModelAndView dashboard() {
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
			new Object[] { PathResources.DASHBOARD, user.getId() }));
		modelAndView.addObject(Parameters.CURRENT_USER, user);
		modelAndView.addObject(Parameters.FULL_NAME, user.getFullname());

		// Comprobamos si existen notificaciones sin leer y añadimos el parámetro al
		// modelo.
		boolean thereAreNotifications = notificationService.userHasNotifications(user.getId());
		modelAndView.addObject(Parameters.THERE_ARE_NOTIFICATIONS, thereAreNotifications);

		// Calculamos los KPIs
		Map<Kpi, Object> kpis = getKpis(user.getId());

		// Vamos recuperando los KPI, lo parseamos y lo insertamos en el modelo.
		try {
		    // KPI 1.
		    List<Vulnerability> kpi1 = (List<Vulnerability>) kpis.get(Kpi.KPI_1);
		    modelAndView.addObject(Kpi.KPI_1.getValue(), kpi1);
		    // KPI 2.
		    Integer kpi2 = (Integer) kpis.get(Kpi.KPI_2);
		    modelAndView.addObject(Kpi.KPI_2.getValue(), kpi2);
		    // KPI 3.
		    Integer kpi3 = (Integer) kpis.get(Kpi.KPI_3);
		    modelAndView.addObject(Kpi.KPI_3.getValue(), kpi3);
		    // KPI 4.
		    Integer kpi4 = (Integer) kpis.get(Kpi.KPI_4);
		    modelAndView.addObject(Kpi.KPI_4.getValue(), kpi4);
		    // KPI 5.
		    Integer kpi5 = (Integer) kpis.get(Kpi.KPI_5);
		    modelAndView.addObject(Kpi.KPI_5.getValue(), kpi5);
		    // KPI 6.
		    List<Tracking> kpi6 = (List<Tracking>) kpis.get(Kpi.KPI_6);
		    modelAndView.addObject(Kpi.KPI_6.getValue(), kpi6);
		    // KPI 7.
		    Map<Tracking, Integer> kpi7 = (Map<Tracking, Integer>) kpis.get(Kpi.KPI_7);
		    List<Tracking> trackings = new ArrayList<Tracking>();
		    trackings.addAll(kpi7.keySet());
		    Object[][] dataKpi7 = new Object[trackings.size() + 1][2];
		    if (!trackings.isEmpty()) {
			dataKpi7[0][0] = I18n.getResource(LanguageKeys.CHART_TRACKINGS, false);
			dataKpi7[0][1] = I18n.getResource(LanguageKeys.CHART_NOTIFICATION_NUMBER, false);
			for (Integer i = 1; i <= trackings.size(); i++) {
			    Tracking t = trackings.get(i - 1);
			    dataKpi7[i][0] = t.getTrackingName();
			    dataKpi7[i][1] = kpi7.get(t);
			}
		    } else {
			dataKpi7 = null;
		    }
		    modelAndView.addObject(Parameters.DATA_KPI_7, dataKpi7);
		    // KPI 8.
		    Map<Tracking, Integer> kpi8 = (Map<Tracking, Integer>) kpis.get(Kpi.KPI_8);
		    List<Tracking> trackings2 = new ArrayList<Tracking>();
		    trackings2.addAll(kpi8.keySet());
		    Object[][] dataKpi8 = new Object[trackings2.size() + 1][2];
		    if (!trackings2.isEmpty()) {
			dataKpi8[0][0] = I18n.getResource(LanguageKeys.CHART_TRACKINGS, false);
			dataKpi8[0][1] = I18n.getResource(LanguageKeys.CHART_VULNERABILITIES_NUMBER, false);
			for (Integer i = 1; i <= trackings2.size(); i++) {
			    Tracking t = trackings2.get(i - 1);
			    dataKpi8[i][0] = t.getTrackingName();
			    dataKpi8[i][1] = kpi8.get(t);
			}
		    } else {
			dataKpi8 = null;
		    }
		    modelAndView.addObject(Parameters.DATA_KPI_8, dataKpi8);

		    // Añadimos la configuración dashboard al modelo.
		    Dashboard dashboard = dashboardService.findByUserId(user);
		    modelAndView.addObject(Parameters.DASHBOARD, dashboard);

		} catch (ClassCastException e) {
		    LOGGER.error(
			    I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_CAST_KPI, true, new Object[] { e }));
		}

		modelAndView.setViewName(PathResources.DASHBOARD);
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
     * Method that maps the request for the resource '/dashboard/save' through of a
     * post HTTP method and save the form in the database.
     * 
     * @param dashboard Object with the data to save.
     * @param errors    Binding object to return errors to form.
     * @return a redirected view of the dashboard.
     */
    @PostMapping(CommonsResources.SLASH + PathResources.DASHBOARD + CommonsResources.SLASH + PathResources.SAVE)
    public ModelAndView save(Dashboard dashboard, BindingResult errors) {
	try {
	    // recuperamos el usuario
	    User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

	    // Recuperamos la configuración dashboard del usuario
	    Dashboard d = dashboardService.findByUserId(user);

	    // Actualizamos los valores que pueden ser editados por el usuario
	    d.setChartOptionKpi7(
		    dashboard.getChartOptionKpi7() != null ? dashboard.getChartOptionKpi7() : d.getChartOptionKpi7());
	    d.setChartOptionKpi8(
		    dashboard.getChartOptionKpi8() != null ? dashboard.getChartOptionKpi8() : d.getChartOptionKpi8());
	    d.setKpi7(dashboard.isKpi7());
	    d.setKpi8(dashboard.isKpi8());

	    // Si hay errores en los parámetros de entrada, los registramos en el log.
	    if (errors.hasErrors()) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_DASHBOARD_SETTING_HAS_ERRORS, true,
			new Object[] { dashboard.getId(), errors.getErrorCount() }));
	    }

	    // Guardamos la nueva configuración.
	    dashboardService.save(d);

	    // Redirigimos a la vista principal de dashboard.
	    return new ModelAndView(
		    Parameters.REDIRECT + CommonsResources.COLON + CommonsResources.SLASH + PathResources.DASHBOARD);
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return new ModelAndView(PathResources.ERROR);
	}
    }

    /**
     * Auxiliary method that calculates the KPIs of the user.
     * 
     * @param userId User identifier.
     * @return a map object with the KPIs calculated.
     */
    private Map<Kpi, Object> getKpis(String userId) {
	try {

	    // Recuperamos la lista de notificaciones.
	    List<Notification> notifications = notificationService.findAllByUser(userId);

	    // Inicializamos el conjunto de variables que vamos a necesitar.
	    List<Notification> uniqueNotification = new ArrayList<Notification>();
	    List<Vulnerability> uniqueVulnerabilities = new ArrayList<Vulnerability>();
	    List<Tracking> tempTracking = new ArrayList<Tracking>();
	    Map<Tracking, Integer> notificationByTracking = new HashMap<Tracking, Integer>();
	    Map<Tracking, Integer> vulsByTrackings = new HashMap<Tracking, Integer>();
	    Integer unreadNotifications = 0;
	    Tracking tracking = null;
	    Vulnerability vulnerability = null;

	    // Guardamos la lista de notificaciones y vulnerabilidades únicas, así como el
	    // número de notificaciones que tiene cada seguimiento (eliminamos las repetidas
	    // con misma vulnerabilidad) y las notificaciones no leidas.
	    if (notifications != null) {
		for (Notification notification : notifications) {
		    tracking = notification.getTracking();
		    vulnerability = notification.getVulnerability();

		    if (!notification.isRead()) {
			unreadNotifications++;
		    }

		    if (notificationByTracking.containsKey(tracking)) {
			Integer nNotification = notificationByTracking.get(tracking);
			notificationByTracking.put(tracking, ++nNotification);
		    } else {
			notificationByTracking.put(tracking, 1);
		    }

		    if (!uniqueVulnerabilities.contains(vulnerability)) {
			uniqueVulnerabilities.add(vulnerability);
			uniqueNotification.add(notification);
			tempTracking.add(tracking);
		    } else if (!tempTracking.contains(tracking)) {
			uniqueNotification.add(notification);
		    }
		}
	    }

	    // Recorremos la lista única de notificaciones para calcular el número de
	    // seguimientos vulnerables y el nuevo de vulnerabilidades que afectan a cada
	    // seguimiento vulnerable.
	    for (Notification notification : uniqueNotification) {
		tracking = notification.getTracking();
		vulnerability = notification.getVulnerability();
		Integer vuls = 0;
		if (vulsByTrackings.containsKey(tracking)) {
		    vuls = vulsByTrackings.get(tracking);
		    vulsByTrackings.put(tracking, ++vuls);
		} else {
		    vulsByTrackings.put(tracking, 1);
		}
	    }
	    List<Tracking> vulnerableTrackings = new ArrayList<Tracking>();
	    vulnerableTrackings.addAll(vulsByTrackings.keySet());

	    // Recuperamos el resto de estadísticas.
	    Integer totalNotifications = notifications != null ? notifications.size() : 0;
	    List<Tracking> trackings = trackingService.findAllByUser(userId);
	    Integer totalTrackings = 0;
	    Integer totalAlarms = 0;
	    if (trackings != null) {
		totalTrackings = trackings.size();
		for (Tracking t : trackings) {
		    totalAlarms += t.getAlarms().size();
		}
	    }
	    // Creamos el objeto que contendrá los resultados, insertamos los KPIs y lo
	    // devolvemos.
	    Map<Kpi, Object> res = new HashMap<Kpi, Object>();
	    // KPI 1. Lista completa de vulnerabilidades que afectan a algún seguimiento.
	    res.put(Kpi.KPI_1, uniqueVulnerabilities);
	    // KPI 2. Número total de notificaciones.
	    res.put(Kpi.KPI_2, totalNotifications);
	    // KPI 3. Número total de notificaciones sin leer.
	    res.put(Kpi.KPI_3, unreadNotifications);
	    // KPI 4. Número total de seguimientos.
	    res.put(Kpi.KPI_4, totalTrackings);
	    // KPI 5. Número total de alarmas configuradas.
	    res.put(Kpi.KPI_5, totalAlarms);
	    // KPI 6. Lista de seguimientos vulnerables.
	    List<Tracking> ts = new ArrayList<Tracking>();
	    ts.addAll(vulsByTrackings.keySet());
	    res.put(Kpi.KPI_6, ts);
	    // KPI 7. Map con el número de notificaciones (value) que tiene cada seguimiento
	    // (key).
	    res.put(Kpi.KPI_7, notificationByTracking);
	    // KPI 8. Map con el número de vulnerabilidades (value) que tienen los
	    // seguimientos (key).
	    res.put(Kpi.KPI_8, vulsByTrackings);
	    // Devolvemos el map.
	    return res;

	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

}
