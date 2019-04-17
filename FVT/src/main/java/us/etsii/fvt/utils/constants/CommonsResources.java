package us.etsii.fvt.utils.constants;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface contains a list of commons resources for all the
 *         project.
 */
public interface CommonsResources {

    /**
     * Constant that represents the language parameter for the internationalization.
     */
    String LANG_PARAM = "lang";

    /**
     * Constant that represents the '/' character.
     */
    String SLASH = "/";

    /**
     * Constant that represents the '**' character.
     */
    String DOUBLE_ASTERISK = "**";

    /**
     * Constant that represents the '?' character.
     */
    String QUESTION = "?";

    /**
     * Constant that represents the '=' character.
     */
    String EQUAL = "=";

    /**
     * Constant that represents the '.' character.
     */
    String DOT = ".";

    /**
     * Constant that represents the ':' character.
     */
    String COLON = ":";

    /**
     * Constant that represents the password pattern.
     */
    String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!#$&=_*+\\-]){9,}.+";

    /**
     * Constant that represents UTF-8 encoding.
     */
    String UTF8 = "UTF-8";

    /**
     * Constant that represents the log message if the messages resources are not
     * accessible.
     */
    String I18N_ERROR_MESSAGE = "AN UNEXPECTED ERROR OCCURRED WHILE IT WAS TRYING TO GET A LANGUAGE MESSAGE";

    /**
     * Constant that represents the Spanish code to internationalize the
     * application.
     */
    String SPANISH_CODE = "es_ES";

    /**
     * Constant that represents the i18n class name.
     */
    String I18N_CLASSNAME = "I18n";

    /**
     * Constant that represents the email validation pattern.
     */
    String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*";

    /**
     * Constant that represents the only number pattern ^(\\d*).
     */
    String ONLY_NUMBER_PATTERN = "^(\\d*)";

    /**
     * Constant that represents the only number pattern \\d*.
     */
    String ONLY_NUMBER_PATTERN_2 = "\\d*";

    /**
     * Constant that represents the '-' character.
     */
    String HYPHEN = "-";

    /**
     * Constant that represents the empty string.
     */
    String EMPTY_STRING = "";

    /**
     * Constant that represents the SMTP protocol name.
     */
    String SMTP = "smtp";

    /**
     * Constant that represents the SMTP socket factory class.
     */
    String SMTP_SSL_SOCKET_CLASS = "javax.net.ssl.SSLSocketFactory";
    
    /**
     * Constant that represents the '\n' character.
     */
    String NEW_LINE = "\n";

    /**
     * Constant that represents the 'unchecked' string.
     */
    String UNCHECKED = "unchecked";

    /**
     * 
     * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
     * 
     *         This interface contains the constants set of parsers.
     */
    public interface parsers {

	/**
	 * Constant that represents the initial XML delimited.
	 */
	String INITIAL_XML_DELIMITED = "<Vulnerability";

	/**
	 * Constant that represents the final XML delimited.
	 */
	String FINAL_XML_DELIMITED = "</cvrfdoc>";

	/**
	 * Constant that represents the 'Vulnerability' constant.
	 */
	String VULNERABILITY = "Vulnerability";

	/**
	 * Constant that represents the 'CVE' constant.
	 */
	String CVE = "CVE";

	/**
	 * Constant that represents the 'Notes' constant.
	 */
	String NOTES = "Notes";

	/**
	 * Constant that represents the 'Note' constant.
	 */
	String NOTE = "Note";

	/**
	 * Constant that represents the 'content' constant.
	 */
	String CONTENT = "content";

	/**
	 * Constant that represents the 'Ordinal' constant.
	 */
	String ORDINAL = "Ordinal";

	/**
	 * Constant that represents the 'References' constant.
	 */
	String REFERENCES = "References";

	/**
	 * Constant that represents the 'Reference' constant.
	 */
	String REFERENCE = "Reference";

	/**
	 * Constant that represents the 'URL' constant.
	 */
	String URL = "URL";

	/**
	 * Constant that represents the 'Description' constant.
	 */
	String DESCRIPTION = "Description";

    }
}
