package us.etsii.fvt.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import us.etsii.fvt.domains.Notification;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.Queries;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface represents the notification repository.
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Method that gets every notification from database for a given user.
     * 
     * @param userId Identifier of the user.
     * @return a list of notifications which belongs to the user.
     */
    @Query(Queries.FIND_ALL_BY_USER)
    List<Notification> findAllByUser(@Param(Parameters.USER_ID) String userId);

    /**
     * Method that gets the list of unread notifications for a given user.
     * 
     * @param userId User identifier.
     * @return a list with the unread notifications.
     */
    @Query(Queries.FIND_ALL_NOTIFICATIONS_UNREAD_BY_USER)
    List<Notification> getAllUnreadNotificationByUser(@Param(Parameters.USER_ID) String userId);

    /**
     * Method that gets the list of notifications related with a given tracking.
     * 
     * @param trackingId Tracking identifier.
     * @return a notifications list which are related with the given tracking.
     */
    @Query(Queries.FIND_ALL_NOTIFICATIONS_BY_TRACKING)
    List<Notification> findAllByTracking(@Param(Parameters.TRACKING_ID) String trackingId);

}
