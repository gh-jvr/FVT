package us.etsii.fvt.utils.schedules;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import us.etsii.fvt.domains.Vulnerability;
import us.etsii.fvt.services.VulnerabilityService;
import us.etsii.fvt.utils.CveUtils;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.Parsers;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the scheduled methods set to synchronize the
 *         vulnerabilities list with the CVE website.
 */
@Component
public class ScheduledCveSync {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(ScheduledCveSync.class);

    /**
     * Constant that represents the default last year.
     */
    private static final Integer DEFAULT_LAST_YEAR = 2008;

    /**
     * CVE utilities class instance.
     */
    private CveUtils cveUtils = new CveUtils();

    /**
     * vulnerability service attribute.
     */
    @Autowired
    private VulnerabilityService vulnerabilityService;

    /**
     * Scheduled task that updates the stored vulnerabilities list.
     */
    @Scheduled(fixedDelayString = Parameters.FIXED_DELAY_IN_MILLISECONDS)
    public void refreshVulnerabilitiesList() {
	try {
	    // obtenemos el año actual en el que nos encontramos.
	    Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

	    // Recuperamos el año de la última vulnerabilidad almacenada.
	    Integer year = getYearLastVulStored();

	    // Si el año es nulo, significa que no existe ningún registro de
	    // vulnerabilidades en la base de datos, por lo tanto, descargamos todas las
	    // listas disponibles.
	    if (year == null) {
		// Recuperamos todos los años disponibles.
		List<Integer> availableYears = cveUtils.getAllAvailableYear();
		if (availableYears != null && !availableYears.isEmpty()) {
		    year = availableYears.get(0);
		} else {
		    year = DEFAULT_LAST_YEAR;
		    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_LAST_YEAR_PROPERTY_NOT_AVAILABLE, true,
			    new Object[] { DEFAULT_LAST_YEAR }));
		}
	    }

	    // Recuperamos la lista de vulnerabilidades desde el último año guardado hasta
	    // el actual.
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_UPDATE_VULNERABILITIES, true,
		    new Object[] { year, currentYear }));

	    // Por cada año, recuperamos la lista de vulnerabilidades, la parseamos y la
	    // almacenamos en base de datos.
	    for (int y = year; y <= currentYear; y++) {
		JSONObject json = cveUtils.getJsonVulnerabilitiesList(String.valueOf(y));
		Vulnerability vul = getLastVulStored();
		Integer seq = vul != null ? vul.getSeq() : null;
		Integer lastyear = vul != null ? Integer.valueOf(vul.getName().split(CommonsResources.HYPHEN)[1])
			: DEFAULT_LAST_YEAR;
		List<Vulnerability> lv = Parsers.parseVulnerabilitiesJson(json, seq, lastyear);
		if (lv != null && !lv.isEmpty()) {
		    vulnerabilityService.saveAll(lv);
		}
	    }
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_VULNERABILITIES_UPDATED, true));

	    // Actualizamos la lista de vulnerabilidades del servicio.
	    vulnerabilityService.refreshAllVuls();
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_VULNERABILITIES_FAILURE, true));
	}
    }

    /**
     * Method that gets the year of the last vulnerabilities list downloaded.
     * 
     * @return the year number of the last list downloaded or null if it doesn't
     *         exists.
     */
    private Integer getYearLastVulStored() {
	try {
	    // Recuperamos la última vulnerabilidad almacenada en base de datos.
	    Vulnerability vulnerability = vulnerabilityService.findLastVulnerabilityStored();

	    // Si la vulnerabilidad es distinta de null, obtenemos el año a partir del
	    // nombre, el cual sigue el formato: 'CVE_year_dddd'. Si no hay ninguna
	    // vulnerabilidad almacenada, devolvemos null.
	    if (vulnerability != null) {
		String year = vulnerability.getName().split(CommonsResources.HYPHEN)[1];
		LOGGER.debug(
			I18n.getResource(LanguageKeys.LogsMessages.LOG_LAST_YEAR_STORED, true, new Object[] { year }));
		return Integer.valueOf(year);
	    } else {
		return null;
	    }
	} catch (ClassCastException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_PARSE_INTEGER, true));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}

    }

    /**
     * Method that gets the last stored vulnerability.
     * 
     * @return the last vulnerability or null in error case.
     */
    private Vulnerability getLastVulStored() {
	try {
	    // Recuperamos la última vulnerabilidad almacenada en la base de datos.
	    Vulnerability vulnerability = vulnerabilityService.findLastVulnerabilityStored();
	    // Si no es nula, devolvemos su número de secuencia.
	    if (vulnerability != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_LAST_VUL_SEQUENCE, true,
			new Object[] { vulnerability.getSeq() }));
		return vulnerability;
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

}
