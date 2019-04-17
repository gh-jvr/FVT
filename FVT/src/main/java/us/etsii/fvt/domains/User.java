package us.etsii.fvt.domains;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the user entity.
 */
@Document(collection = Parameters.Collections.USER)
public class User extends Entity implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -840258550237596930L;

    /**
     * Email attribute. It represents the email address of the user.
     */
    @Email(message = LanguageKeys.SpringValidationMessages.ERROR_EMAIL_INVALID_FORMAT)
    @NotNull(message = LanguageKeys.SpringValidationMessages.ERROR_EMAIL_NULL)
    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String email;

    /**
     * Password attribute. It represents the password of the user.
     */
    @Size(min = 9, max = 50, message = LanguageKeys.SpringValidationMessages.ERROR_PASSWORD_SIZE_INVALID)
    @NotNull(message = LanguageKeys.SpringValidationMessages.ERROR_PASSWORD_NULL)
    @Pattern(regexp = CommonsResources.PASSWORD_PATTERN, message = LanguageKeys.SpringValidationMessages.ERROR_PASSWORD_PATTERN_NOT_MATCH)
    private String password;

    /**
     * Full name attribute. It represents the full name of the user.
     */
    @NotBlank(message = LanguageKeys.SpringValidationMessages.ERROR_FULLNAME_BLANK)
    private String fullname;

    /**
     * Enabled attribute. It represents if a user is enabled or not, that is, if a
     * user can log in or not.
     */
    private boolean enabled;

    /**
     * Roles relational attribute. It represents the set of roles belong to the
     * user.
     */
    @DBRef
    private Set<Role> roles;

    /**
     * email getter method.
     * 
     * @return the email
     */
    public String getEmail() {
	return email;
    }

    /**
     * email setter method.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
	this.email = email;
    }

    /**
     * password getter method.
     * 
     * @return the password
     */
    public String getPassword() {
	return password;
    }

    /**
     * password setter method.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * fullname getter method.
     * 
     * @return the fullname
     */
    public String getFullname() {
	return fullname;
    }

    /**
     * fullname setter method.
     * 
     * @param fullname the fullname to set
     */
    public void setFullname(String fullname) {
	this.fullname = fullname;
    }

    /**
     * enabled getter method.
     * 
     * @return the enabled
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * enabled setter method.
     * 
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    /**
     * roles getter method.
     * 
     * @return the roles
     */
    public Set<Role> getRoles() {
	return roles;
    }

    /**
     * roles setter method.
     * 
     * @param roles the roles to set
     */
    public void setRoles(Set<Role> roles) {
	this.roles = roles;
    }
}
