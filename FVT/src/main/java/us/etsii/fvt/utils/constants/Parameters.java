package us.etsii.fvt.utils.constants;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface contains the resources needed for the parameters of
 *         the application.
 */
public interface Parameters {

    /**
     * Constant that represents the 'email' parameter.
     */
    String EMAIL = "email";

    /**
     * Constant that represents the 'password' parameter.
     */
    String PASSWORD = "password";

    /**
     * Constant that represents the 'currentUser' parameter.
     */
    String CURRENT_USER = "currentUser";

    /**
     * Constant that represents the 'fullName' parameter.
     */
    String FULL_NAME = "fullName";

    /**
     * Constant that represents the 'user' parameter.
     */
    String USER = "user";

    /**
     * Constant that represents the 'successMessage' parameter.
     */
    String SUCCESS_MESSAGE = "successMessage";

    /**
     * Constant that represents the 'trackings' parameter.
     */
    String TRACKINGS = "trackings";

    /**
     * Constant that represents the 'trackingName' parameter.
     */
    String TRACKING_NAME = "trackingName";

    /**
     * Constant that represents the 'redirect' parameter.
     */
    String REDIRECT = "redirect";

    /**
     * Constant that represents the '{id}' parameter.
     */
    String ID_PARAM = "{id}";

    /**
     * Constant that represents the '{trackingId}' parameter.
     */
    String TRACKING_ID_PARAM = "{trackingId}";

    /**
     * Constant that represents the 'userId' parameter.
     */
    String USER_ID = "userId";

    /**
     * Constant that represents the 'alarms' parameter.
     */
    String ALARMS = "alarms";

    /**
     * Constant that represents the 'trackingId' parameter.
     */
    String TRACKING_ID = "trackingId";

    /**
     * Constant that represents the 'enableDisable' parameter.
     */
    String ENABLE_DISABLE = "{enableDisable}";

    /**
     * Constant that represents the 'errors' parameter.
     */
    String ERRORS = "errors";

    /**
     * Constant that represents the 'infos' parameter.
     */
    String INFOS = "infos";

    /**
     * Constant that represents the 'problems' parameter.
     */
    String PROBLEMS = "problems";

    /**
     * Constant that represents the 'softwareName' parameter.
     */
    String SOFTWARE_NAME = "softwareName";

    /**
     * Constant that represents the 'setting' parameter.
     */
    String SETTING = "setting";

    /**
     * Constant that represents the 'defaultAlarm' parameter.
     */
    String DEFAULT_ALARM = "defaultAlarm";

    /**
     * Constant that represents the 'page' parameter.
     */
    String PAGE = "page";

    /**
     * Constant that represents the 'size' parameter.
     */
    String SIZE = "size";

    /**
     * Constant that represents the 'vulsPage' parameter.
     */
    String VULS_PAGE = "vulsPage";

    /**
     * Constant that represents the 'years' parameter.
     */
    String YEARS = "years";

    /**
     * Constant that represents the 'searchVulForm' parameter.
     */
    String SEARCH_VUL_FORM = "searchVulForm";

    /**
     * Constant that represents the 'periods' parameter.
     */
    String PERDIODS = "periods";

    /**
     * Constant that represents the 'notsPage' parameter.
     */
    String NOTS_PAGE = "notsPage";

    /**
     * Constant that represents the 'thereAreNotifications' parameter.
     */
    String THERE_ARE_NOTIFICATIONS = "thereAreNotifications";

    /**
     * Constant that represents the 'mail.smtp.host' parameter.
     */
    Object SMTP_HOST = "mail.smtp.host";

    /**
     * Constant that represents the 'mail.transport.protocol' parameter.
     */
    String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    
    /**
     * Constant that represents the 'mail.smtp.auth' parameter.
     */
    Object SMTP_AUTH = "mail.smtp.auth";

    /**
     * Constant that represents the 'mail.smtp.port' parameter.
     */
    Object SMTP_PORT = "mail.smtp.port";

    /**
     * Constant that represents the 'mail.smtp.socketFactory.port' parameter.
     */
    Object SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";

    /**
     * Constant that represents the 'mail.smtp.socketFactory.class' parameter.
     */
    Object SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";

    /**
     * Constant that represents the 'mail.smtp.socketFactory.fallback' parameter.
     */
    Object SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

    /**
     * Constant that represents the 'dataKpi7' parameter.
     */
    String DATA_KPI_7 = "dataKpi7";

    /**
     * Constant that represents the 'dataKpi8' parameter.
     */
    String DATA_KPI_8 = "dataKpi8";

    /**
     * Constant that represents the 'dashboard' parameter.
     */
    String DASHBOARD = "dashboard";

    /**
     * Constant that represents the 'seq' parameter.
     */
    String SEQ = "seq";

    /**
     * Constant that represents the 'creationDate' parameter.
     */
    String CREATION_DATE = "creationDate";

    /**
     * Constant that represents the '${initial.delay.one.hour}' parameter.
     */
    String INITIAL_DELAY_ONE_HOUR = "${initial.delay.one.hour}";

    /**
     * Constant that represents the '${fixed.delay.one.hour}' parameter.
     */
    String FIXED_DELAY_ONE_HOUR = "${fixed.delay.one.hour}";

    /**
     * Constant that represents the '${initial.delay.six.hours}' parameter.
     */
    String INITIAL_DELAY_SIX_HOURS = "${initial.delay.six.hours}";

    /**
     * Constant that represents the '${fixed.delay.six.hours}' parameter.
     */
    String FIXED_DELAY_SIX_HOURS = "${fixed.delay.six.hours}";

    /**
     * Constant that represents the '${initial.delay.one.day}' parameter.
     */
    String INITIAL_DELAY_ONE_DAY = "${initial.delay.one.day}";

    /**
     * Constant that represents the '${fixed.delay.one.day}' parameter.
     */
    String FIXED_DELAY_ONE_DAY = "${fixed.delay.one.day}";

    /**
     * Constant that represents the '${initial.delay.one.week}' parameter.
     */
    String INITIAL_DELAY_ONE_WEEK = "${initial.delay.one.week}";

    /**
     * Constant that represents the '${fixed.delay.one.week}' parameter.
     */
    String FIXED_DELAY_ONE_WEEK = "${fixed.delay.one.week}";

    /**
     * Constant that represents the '${initial.delay.one.month}' parameter.
     */
    String INITIAL_DELAY_ONE_MONTH = "${initial.delay.one.month}";

    /**
     * Constant that represents the '${fixed.delay.one.month}' parameter.
     */
    String FIXED_DELAY_ONE_MONTH = "${fixed.delay.one.month}";

    /**
     * Constant that represents the '${initial.delay.six.months}' parameter.
     */
    String INITIAL_DELAY_SIX_MONTHS = "${initial.delay.six.months}";

    /**
     * Constant that represents the '${fixed.delay.six.months}' parameter.
     */
    String FIXED_DELAY_SIX_MONTHS = "${fixed.delay.six.months}";

    /**
     * Constant that represents the '${fixedDelay.in.milliseconds}' parameter.
     */
    String FIXED_DELAY_IN_MILLISECONDS = "${fixedDelay.in.milliseconds}";

    /**
     * 
     * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
     * 
     *         This interface contains the collections name of the database mongoDB.
     */
    public interface Collections {

	/**
	 * Constant that represents the 'alarms' collection.
	 */
	String ALARMS = "alarms";

	/**
	 * Constant that represents the 'role' collection.
	 */
	String ROLE = "role";

	/**
	 * Constant that represents the 'trackings' collection.
	 */
	String TRACKINGS = "trackings";

	/**
	 * Constant that represents the 'user' collection.
	 */
	String USER = "user";

	/**
	 * Constant that represents the 'settings' collection.
	 */
	String SETTINGS = "settings";

	/**
	 * Constant that represents the 'vulnerabilities' collection.
	 */
	String VULNERABILITIES = "vulnerabilities";

	/**
	 * Constant that represents the 'notifications' collection.
	 */
	String NOTIFICATIONS = "notifications";
	
	/**
	 * Constant that represents the 'dashboards' collection.
	 */
	String DASHBOARDS = "dashboards";
    }
}
