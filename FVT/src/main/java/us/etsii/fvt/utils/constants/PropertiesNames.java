package us.etsii.fvt.utils.constants;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface contains the list of properties names.
 */
public interface PropertiesNames {

    /**
     * Constant that represents the base name for the properties related with the
     * year of the vulnerabilities list.
     */
    String CVE_RESOURCES_BY_YEAR_PREFIX = "res_year_";

    /**
     * Constant that represents the properties file name.
     */
    String STATIC_FILE_NAME = "preferences.properties";

    /**
     * Constant that represents the 'cveUrl' property.
     */
    String CVE_URL = "cveUrl";

    /**
     * Constant that represents the 'smtpHost' property.
     */
    String SMTP_HOST = "smtpHost";

    /**
     * Constant that represents the 'smtpAuth' property.
     */
    String SMTP_AUTH = "smtpAuth";

    /**
     * Constant that represents the 'smtpPort' property.
     */
    String SMTP_PORT = "smtpPort";

    /**
     * Constant that represents the 'smtpPortSSL' property.
     */
    String SMTP_SSL_PORT = "smtpPortSSL";

    /**
     * Constant that represents the 'smtpSSLFallback' property.
     */
    String SMTP_SOCKET_FACTORY_FALLBACK = "smtpSSLFallback";

    /**
     * Constant that represents the 'smtpFrom' property.
     */
    String SMTP_FROM = "smtpFrom";

    /**
     * Constant that represents the 'smtpPswd' property.
     */
    String SMTP_PASSWORD = "smtpPswd";

    /**
     * Constant that represents the 'Resources' path fraction where the external
     * properties file is.
     */
    String STATIC_FILE_PROPS_PATH_1 = "Resources";

    /**
     * Constant that represents the 'preferences.properties' path fraction where the
     * external properties file is.
     */
    String STATIC_FILE_PROPS_PATH_2 = "preferences.properties";

    /**
     * Constant that represents the 'log4j.path' property.
     */
    String LOG4J_PATH = "log4j.path";

}
