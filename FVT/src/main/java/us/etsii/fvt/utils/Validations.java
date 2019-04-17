package us.etsii.fvt.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.etsii.fvt.domains.Alarm;
import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.enums.Period;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents a set of method used to validate object.
 */
public class Validations {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(Validations.class);

    /**
     * Constant that represents the list of valid periods.
     */
    private static final List<Integer> PERIODS = Period.getAllValues();

    /**
     * Method that checks if the setting parameters are valid.
     * 
     * @param setting Object to validate.
     * @return a list of error messages. If the list is empty, it means that there
     *         are no errors.
     */
    public static List<String> validateSetting(Setting setting) {
	List<String> res = new ArrayList<String>();
	if (setting.getEmail() != null && !setting.getEmail().isEmpty()
		&& !setting.getEmail().matches(CommonsResources.EMAIL_PATTERN)) {
	    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_VAL_INVALID_EMAIL, true,
		    new Object[] { setting.getEmail(), CommonsResources.EMAIL_PATTERN }));
	    res.add(I18n.getResource(LanguageKeys.SETTING_VALIDATION_INVALID_EMAIL, false));
	}
	return res;
    }

    /**
     * Method that checks if an alarm object has all is values correctly.
     * 
     * @param alarm Object to evaluate.
     * @return True if the object is valid and False if not.
     */
    public static boolean checkAlarm(Alarm alarm) {
	try {
	    // Comprobamos si es nula.
	    if (alarm == null) {
		LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_VALIDATION_ALARM_NULL, true));
		return false;
	    }
	    // Comprobamos que el nombre no sea nulo ni este vacío.
	    if (alarm.getAlarmName() == null || alarm.getAlarmName().trim().isEmpty()) {
		LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_VALIDATION_ALARM_NAME_EMPTY, true));
		return false;
	    }
	    // Comprobamos que el periodo de la alarma no sea nulo y coincida con alguno de
	    // los posibles valores.
	    if (alarm.getAlarmPeriod() == null) {
		LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_VALIDATION_ALARM_PERIOD_NULL, true));
		return false;
	    } else {
		if (!PERIODS.contains(alarm.getAlarmPeriod())) {
		    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_VALIDATION_ALARM_PERIOD_NOT_VALID,
			    true));
		    return false;
		}
	    }
	    // Si ha pasado todas las comprobaciones anteriores, la alarma es válida.
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_ALARM_VALIDATION_ALARM_VALID, true));
	    return true;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }
}
