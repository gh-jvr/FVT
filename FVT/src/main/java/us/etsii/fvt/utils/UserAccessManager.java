package us.etsii.fvt.utils;

import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.exceptions.UnauthorizedAccessException;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the control of the user access to resources.
 */
public class UserAccessManager {

    /**
     * Method that checks if the user that has permission to do the operation.
     * 
     * @param userId   User identifier.
     * @param objectId user identifier of the object.
     * @throws UnauthorizedAccessException if the user has not access to the operation.
     */
    public static void userAccess(String userId, String objectId) throws UnauthorizedAccessException {
	if (!objectId.equals(userId)) {
	    throw new UnauthorizedAccessException(
		    I18n.getResource(LanguageKeys.USER_ACCESS_EXCEPTION, false, new Object[] { userId, objectId }));
	}
    }

}
