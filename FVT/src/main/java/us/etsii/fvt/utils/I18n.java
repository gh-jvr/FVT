package us.etsii.fvt.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;

import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.PathResources;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the internationalization set tools.
 */
public class I18n {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(I18n.class);

    /**
     * Constant that represents the path to the language properties files.
     */
    private static final String[] PROPERTIES_FILES = new String[] { PathResources.STATIC + CommonsResources.SLASH
	    + PathResources.MESSAGES + CommonsResources.SLASH + PathResources.LANG_MESSAGES };

    /**
     * Method that obtains the value of a property given its key.
     * 
     * @param key          The property's identifier.
     * @param forceSpanish Param that indicates if the messages must be search in
     *                     Spanish or in the configured language. This is used to
     *                     write log messages.
     * @return a String with the value of the property.
     */
    public static String getResource(String key, boolean forceSpanish) {
	// Recorremos todos lo ficheros de propiedades que existan.
	for (int i = 0; i < PROPERTIES_FILES.length; i++) {
	    try {
		// Devolvemos el valor de la propiedad
		if (forceSpanish) {
		    return ResourceBundle.getBundle(PROPERTIES_FILES[i], new Locale(CommonsResources.SPANISH_CODE))
			    .getString(key);
		} else {
		    return ResourceBundle.getBundle(PROPERTIES_FILES[i], LocaleContextHolder.getLocale())
			    .getString(key);
		}
	    } catch (MissingResourceException e) {
		if (i >= PROPERTIES_FILES.length) {
		    throw new MissingResourceException(e.getMessage(), CommonsResources.I18N_CLASSNAME, key);
		}
	    } catch (Exception e) {
		LOGGER.error(CommonsResources.I18N_ERROR_MESSAGE, e);
	    }
	}
	return null;
    }

    /**
     * Method that obtains the value of a property given its key and a array of
     * objects.
     * 
     * @param key          The property's identifier.
     * @param forceSpanish Param that indicates if the messages must be search in
     *                     Spanish or in the configured language. This is used to
     *                     write log messages.
     * @param params       Array of object with parameters to insert in the string.
     * @return a String with the value of the property and the objects.
     */
    public static String getResource(String key, boolean forceSpanish, Object[] params) {
	// Recorremos todos lo ficheros de propiedades
	for (String file : PROPERTIES_FILES) {
	    try {
		// Devolvemos la cadena formateada con los parametros ya
		// insertados.
		if (forceSpanish) {
		    return MessageFormat.format(ResourceBundle.getBundle(file, new Locale(CommonsResources.SPANISH_CODE)).getString(key),
			    params);
		} else {
		    return MessageFormat.format(
			    ResourceBundle.getBundle(file, LocaleContextHolder.getLocale()).getString(key), params);
		}
	    } catch (MissingResourceException e) {
		throw new MissingResourceException(e.getMessage(), CommonsResources.I18N_CLASSNAME, key);
	    } catch (Exception e) {
		LOGGER.error(CommonsResources.I18N_ERROR_MESSAGE, e);
	    }
	}
	return null;
    }
}
