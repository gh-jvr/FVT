package us.etsii.fvt.utils.exceptions;

import us.etsii.fvt.utils.I18n;
import us.etsii.fvt.utils.constants.LanguageKeys;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the exception Unauthorized access. The
 *         exception will trigger when an user tries to access to a unauthorized
 *         resource.
 */
public class UnauthorizedAccessException extends Exception {

    /**
     * The serial Version UID.
     */
    private static final long serialVersionUID = -6218122181827028356L;

    /**
     * Default constructor.
     */
    public UnauthorizedAccessException() {
	super(I18n.getResource(LanguageKeys.EXCEPTION_UNAUTHORIZED_ACCESS_CAUSE, false));
    }

    /**
     * Default constructor with parameter.
     * 
     * @param message Exception message.
     */
    public UnauthorizedAccessException(String message) {
	super(message);
    }

}
