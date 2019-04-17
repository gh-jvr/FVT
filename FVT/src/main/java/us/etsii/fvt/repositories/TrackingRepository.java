package us.etsii.fvt.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import us.etsii.fvt.domains.Tracking;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.Queries;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface represents the tracking repository.
 */
@Repository
public interface TrackingRepository extends MongoRepository<Tracking, String> {

    /**
     * Method that gets every tracking from database for a given user.
     * 
     * @param userId Identifier of the user.
     * @return a list of tracking which belongs to the user.
     */
    @Query(Queries.FIND_ALL_BY_USER)
    List<Tracking> findAllByUser(@Param(Parameters.USER_ID) String userId);

}
