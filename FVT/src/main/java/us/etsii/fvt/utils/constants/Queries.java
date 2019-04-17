package us.etsii.fvt.utils.constants;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface contains the MongoDB queries.
 */
public interface Queries {

    /**
     * Query that finds a list of element by its user ID.
     */
    String FIND_ALL_BY_USER = "{ 'user.id' : ?0 }";

    /**
     * Query that represents the empty set.
     */
    String EMPTY_SET = "{ }";

    /**
     * Query that finds a list of notifications unread for the given user.
     */
    String FIND_ALL_NOTIFICATIONS_UNREAD_BY_USER = "{ '$and': [ {'user.id' : ?0},{'read' : false} ] }";


    /**
     * Query that finds a list of notifications that belong to a tracking.
     */
    String FIND_ALL_NOTIFICATIONS_BY_TRACKING = "{ 'tracking.id' : ?0 }";

}
