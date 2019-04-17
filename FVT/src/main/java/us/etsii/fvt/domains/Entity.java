package us.etsii.fvt.domains;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This abstract class contains the commons attributes for each entity
 *         of the project.
 */
public abstract class Entity {

    /**
     * Id attribute. It represents the Identifier of the entity in the database.
     */
    @Id
    private String id;

    /**
     * id getter method.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * id setter method.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Hash code method.
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    /**
     * Equals method.
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Entity other = (Entity) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

    /**
     * To string method.
     */
    @Override
    public String toString() {
	return "Domain [id=" + id + "]";
    }
}
