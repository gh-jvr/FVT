package us.etsii.fvt.utils.exceptions;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the exception of resource not available. The
 *         exception will trigger when the resource required is not available in
 *         database.
 */
public class ResourceNotAvailableException extends Exception {


    /**
     * Serial verison UID.
     */
    private static final long serialVersionUID = 7640830149752253974L;

    /**
     * Default constructor with parameter.
     * 
     * @param message Exception message.
     */
    public ResourceNotAvailableException(String message) {
	super(message);
    }

}
