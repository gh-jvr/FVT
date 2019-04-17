package us.etsii.fvt.domains;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import us.etsii.fvt.utils.constants.Parameters;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the Role entity.
 */
@Document(collection = Parameters.Collections.ROLE)
public class Role extends Entity implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3380644962331759532L;
    /**
     * Role attribute. It represents the role the user in the system.
     */
    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String role;

    /**
     * role getter method.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * role setter method.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

}
