package us.etsii.fvt.domains;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import us.etsii.fvt.utils.constants.Parameters;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the setting entity.
 */
@Document(collection = Parameters.Collections.SETTINGS)
public class Setting extends Entity implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 366112033227255828L;

    /**
     * Email address where the notification will be sent.
     */
    private String email;

    /**
     * Alarm relational attribute that can be used as default alarm.
     */
    @DBRef
    private Alarm defaultAlarm;

    /**
     * User relational attribute. It represents the user which belongs this setting
     * object.
     */
    @DBRef
    private User user;

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
     * defaultAlarm getter method.
     * 
     * @return the defaultAlarm
     */
    public Alarm getDefaultAlarm() {
	return defaultAlarm;
    }

    /**
     * defaultAlarm setter method.
     * 
     * @param defaultAlarm the defaultAlarm to set
     */
    public void setDefaultAlarm(Alarm defaultAlarm) {
	this.defaultAlarm = defaultAlarm;
    }

    /**
     * user getter method.
     * 
     * @return the user
     */
    public User getUser() {
	return user;
    }

    /**
     * user setter method.
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
	this.user = user;
    }

}
