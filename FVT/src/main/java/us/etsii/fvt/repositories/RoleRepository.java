package us.etsii.fvt.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.etsii.fvt.domains.Role;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This interface represents the Role repository.
 */
public interface RoleRepository extends MongoRepository<Role, String> {

    /**
     * Method that finds a role by the string role representation.
     * 
     * @param role String representation of the role.
     * @return a role object which matches with the given name.
     */
    Role findByRole(String role);
}
