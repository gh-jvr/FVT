package us.etsii.fvt.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.etsii.fvt.domains.User;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This Interface represents the user repository.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Method that finds a user by its email.
     * 
     * @param email String representation of its email.
     * @return the user whose email matches with the given one.
     */
    User findByEmail(String email);

}
