package us.etsii.fvt.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.constants.Queries;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface represents the setting repository.
 */
@Repository
public interface SettingRepository extends MongoRepository<Setting, String> {

    /**
     * Method that gets the setting data from database for a given user.
     * 
     * @param id User identifier.
     * @return the user setting object.
     */
    @Query(Queries.FIND_ALL_BY_USER)
    Setting findByUserId(@Param(Parameters.USER_ID) String id);

}
