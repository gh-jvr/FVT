package us.etsii.fvt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.PropertiesNames;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the methods set to input/output operations.
 */
public class Io {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(Io.class);

    /**
     * Constant that contains the set of properties of the file
     * preferences.properties.
     */
    private static final Properties STATIC_PROPERTIES;

    // Cargamos las propiedades del fichero.
    static {
	STATIC_PROPERTIES = loadStaticProperties();
    }

    /**
     * Method that gets the CVE url from the properties file.
     * 
     * @return a String with the URL declared in the properties file.
     */
    public static String getCveUrl() {
	return searchProperty(PropertiesNames.CVE_URL);
    }

    /**
     * Method that gets the CVE resource from the properties file.
     * 
     * @param year Year of the resource to obtain.
     * @return a String with the resource declared in the properties file.
     */
    public static String getCveResource(String year) {
	return searchProperty(year);
    }

    /**
     * Method that gets the SMTP host name from the properties file.
     * 
     * @return a String which represents the SMTP host name.
     */
    public static String getSmtpHost() {
	return searchProperty(PropertiesNames.SMTP_HOST);
    }

    /**
     * Method that gets the SMTP authentication boolean value from the properties
     * file.
     * 
     * @return a String which represents if the SMTP connection must be with
     *         authentication (true) or not (false).
     */
    public static String getSmtpAuthentication() {
	String prop = searchProperty(PropertiesNames.SMTP_AUTH);
	try {
	    Boolean.getBoolean(prop);
	    return prop;
	} catch (ClassCastException e) {
	    throw new ClassCastException(I18n.getResource(LanguageKeys.ERROR_CAST_PROPERTY, false,
		    new Object[] { PropertiesNames.SMTP_AUTH }));
	}
    }

    /**
     * Method that gets the SMTP port number from the properties file.
     * 
     * @return a String which represents the SMTP connection port.
     */
    public static String getSmtpPort() {
	return searchProperty(PropertiesNames.SMTP_PORT);
    }

    /**
     * Method that gets the SSL SMTP port number from the properties file.
     * 
     * @return a String which represents the SSL SMTP connection port.
     */
    public static String getSmtpSslPort() {
	return searchProperty(PropertiesNames.SMTP_SSL_PORT);
    }

    /**
     * Method that gets if the socket factory fallback is enabled or not.
     * 
     * @return a String representation of the boolean that indicates if the fallback
     *         is enabled (true) or not (false).
     */
    public static String getSmtpSocketFactoryFallback() {
	String prop = searchProperty(PropertiesNames.SMTP_SOCKET_FACTORY_FALLBACK);
	try {
	    Boolean.getBoolean(prop);
	    return prop;
	} catch (ClassCastException e) {
	    throw new ClassCastException(I18n.getResource(LanguageKeys.ERROR_CAST_PROPERTY, false,
		    new Object[] { PropertiesNames.SMTP_SOCKET_FACTORY_FALLBACK }));
	}
    }

    /**
     * Method that gets the SMTP sender email (from).
     * 
     * @return the sender email.
     */
    public static String getSmtpFrom() {
	return searchProperty(PropertiesNames.SMTP_FROM);
    }

    /**
     * Method that gets the SMTP sender password from the properties file.
     * 
     * @return the sender account password.
     */
    public static String getSmtpPassword() {
	return searchProperty(PropertiesNames.SMTP_PASSWORD);
    }

    /**
     * Method that gets the log4j file path from the properties file.
     * 
     * @return the path where the log files will be created.
     */
    public static String getLog4jProperty() {
	return searchProperty(PropertiesNames.LOG4J_PATH);
    }

    /**
     * Method that searches a set of properties which match with a given pattern.
     * 
     * @param pattern regular expression to match with the properties.
     * @return the list of values found or null if it did not find any property.
     */
    public static List<String> searchSetProperties(String pattern) {
	List<String> res = new ArrayList<>();
	for (String propName : STATIC_PROPERTIES.stringPropertyNames()) {
	    if (propName.matches(pattern)) {
		res.add(propName);
	    }
	}
	return res;
    }

    /**
     * Method that get the value of a property from the preferences file.
     * 
     * @return a String with the value requested.
     */
    private static String searchProperty(String keyToSearch) {
	return STATIC_PROPERTIES.getProperty(keyToSearch);
    }

    /**
     * Method that load the static properties from the file of preferences.
     * 
     * @return a properties object with the static properties.
     */
    private static Properties loadStaticProperties() {
	Properties res = new Properties();
	try {
	    // Buscamos si existe el fichero de propiedades dentro del proyecto.
	    InputStream props = Io.class.getClassLoader().getResourceAsStream(PropertiesNames.STATIC_FILE_NAME);

	    // Si el fichero de propiedades está dentro del proyecto, usamos dicho fichero,
	    // sino, buscamos el fichero en la ruta externa predeterminada.
	    if (props == null) {
		String properties = CommonsResources.DOT + File.separator + PropertiesNames.STATIC_FILE_PROPS_PATH_1
			+ File.separator + PropertiesNames.STATIC_FILE_PROPS_PATH_2;
		final FileInputStream in = new FileInputStream(properties);
		res.load(in);
	    } else {
		res.load(props);
	    }
	} catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	return res;
    }
}
