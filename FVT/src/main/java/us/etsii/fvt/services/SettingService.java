package us.etsii.fvt.services;

import java.util.Optional;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.repositories.SettingRepository;
import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the setting service.
 */
@Service
@Transactional
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(SettingService.class);

    /**
     * Method that finds the setting of a user if it exists.
     * 
     * @param id User identifier.
     * @return The setting object of the user or Null if it doesn't exists.
     */
    public Setting findByUserId(String id) {
	try {
	    LOGGER.debug(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_FIND_BY_USER, true, new Object[] { id }));
	    Setting setting = settingRepository.findByUserId(id);
	    if (setting != null && setting.getId() != null) {
		LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_FOUND, true,
			new Object[] { setting.getId() }));
	    } else {
		LOGGER.warn(
			I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_NOT_FOUND, true, new Object[] { id }));
	    }
	    return setting;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Method that updates a setting in the database.
     * 
     * @param id      Setting identifier.
     * @param setting New setting object to replace for the old one.
     * @return True if the task has been finished successfully, False in other case.
     */
    public boolean updateSetting(String id, Setting setting) {
	try {
	    LOGGER.debug(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_UPDATE, true, new Object[] { id, setting }));
	    if (setting == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_NULL, true));
		return false;
	    }
	    // Buscamos la configuración almacenada en BD.
	    Optional<Setting> OptSetting = settingRepository.findById(id);
	    if (OptSetting.isPresent()) {
		Setting s = OptSetting.get();
		// Comprobamos campo por campo y actualizamos unicamente aquellos que sea
		// necesario.
		s.setDefaultAlarm(s.getDefaultAlarm() == null || !s.getDefaultAlarm().equals(setting.getDefaultAlarm())
			? setting.getDefaultAlarm()
			: s.getDefaultAlarm());
		s.setEmail(s.getEmail() == null || !s.getEmail().equals(setting.getEmail()) ? setting.getEmail()
			: s.getEmail());
		saveSetting(s);
		LOGGER.debug(
			I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_UPDATED, true, new Object[] { id }));
		return true;
	    } else {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_NOT_EXISTS, true,
			new Object[] { id }));
		return false;
	    }
	} catch (IllegalArgumentException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_UPDATE_ID_NULL, true));
	    return false;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

    /**
     * Method that save a setting into the database.
     * 
     * @param setting New setting object to save.
     * @return True if the task has been finished correctly, False in other case.
     */
    public boolean saveSetting(@Valid Setting setting) {
	try {
	    if (setting == null) {
		LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_SETTING_NULL, true));
		return false;
	    }
	    settingRepository.save(setting);
	    LOGGER.info(I18n.getResource(LanguageKeys.LogsMessages.LOG_SETTING_SAVED_SUCCESSFULLY_SERVICE, true,
		    new Object[] { setting.getId() }));
	    return true;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return false;
	}
    }

}
