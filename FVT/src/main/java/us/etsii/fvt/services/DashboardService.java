package us.etsii.fvt.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.etsii.fvt.domains.Dashboard;
import us.etsii.fvt.domains.User;
import us.etsii.fvt.repositories.DashboardRepository;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.enums.Chart;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the Dashboard service.
 */
@Service
@Transactional
public class DashboardService {

    /**
     * Dashboard repository attribute.
     */
    @Autowired
    private DashboardRepository dashboardRepository;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(DashboardService.class);

    /**
     * Method that get the dashboard object of the given user. If the dashboard
     * object doesn't exist, it will create a new one with the default values.
     * 
     * @param user User object.
     * @return the dashboard object stored in database (or the new dashboard
     *         created) or null if the user is not valid.
     */
    public Dashboard findByUserId(User user) {
	try {
	    // Si el usuario o el identificador es nulo, lanzamos excepción.
	    if (user == null || user.getId() == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.ERROR_USER_ID_NULL, false));
	    }

	    // Buscamos el dashboard en base de datos.
	    Dashboard dashboard = dashboardRepository.findByUserId(user.getId());

	    // Si el objeto es nulo, creamos uno nuevo para el usuario con los parámetros
	    // por defecto.
	    if (dashboard == null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_CREATE_DEFAULT_DASHBOARD, true));
		dashboard = new Dashboard();
		dashboard.setChartOptionKpi7(Chart.LINE.getValue());
		dashboard.setKpi7(false);
		dashboard.setChartOptionKpi8(Chart.LINE.getValue());
		dashboard.setKpi8(false);
		dashboard.setUser(user);
		dashboard = save(dashboard);
	    }

	    // Comprobamos que los valores de los radio buttons son correctos.
	    List<String> options = new ArrayList<String>();
	    options.add(Chart.LINE.getValue());
	    options.add(Chart.BAR.getValue());
	    options.add(Chart.PIE.getValue());
	    if (dashboard.getChartOptionKpi7() != null) {
		if (!options.contains(dashboard.getChartOptionKpi7())) {
		    // Si el valor recibido no coincide con ninguna de las opciones, la cambiamos a
		    // su valor por defecto.
		    dashboard.setChartOptionKpi7(Chart.LINE.getValue());
		}
	    }
	    if (dashboard.getChartOptionKpi8() != null) {
		if (!options.contains(dashboard.getChartOptionKpi8())) {
		    // Si el valor recibido no coincide con ninguna de las opciones, la cambiamos a
		    // su valor por defecto.
		    dashboard.setChartOptionKpi8(Chart.LINE.getValue());
		}
	    }

	    // Devolvemos el objeto.
	    return dashboard;
	} catch (NullPointerException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_DASHBOARD_NOT_FOUND, true,
		    new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that save a dashboard object into database.
     * 
     * @param dashboard Object to save.
     * @return The stored dashboard object or null in error case.
     */
    public Dashboard save(Dashboard dashboard) {
	try {
	    // Si el objeto dashboard es nulo, lanzamos excepción.
	    if (dashboard == null) {
		throw new NullPointerException(I18n.getResource(LanguageKeys.ERROR_DASHBOARD_NULL, false));
	    }

	    // Guardamos el objeto.
	    return dashboardRepository.save(dashboard);
	} catch (NullPointerException e) {
	    LOGGER.error(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_DASHBOARD_NOT_SAVE, true, new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}

    }

}
