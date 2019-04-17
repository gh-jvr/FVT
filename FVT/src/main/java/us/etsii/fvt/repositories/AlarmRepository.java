package us.etsii.fvt.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import us.etsii.fvt.domains.Alarm;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface represents the alarm repository.
 */
@Repository
public interface AlarmRepository extends MongoRepository<Alarm, String> {

}
