package us.etsii.fvt.domains;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import us.etsii.fvt.utils.constants.Parameters;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the notification entity.
 */
@Document(collection = Parameters.Collections.NOTIFICATIONS)
public class Notification extends Entity implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7758746307441413643L;

    /**
     * Attribute that represents the date in which the notification was created.
     */
    @NotNull
    @DateTimeFormat
    private LocalDateTime creationDate;

    /**
     * Attribute that represents if the notification has been read by the user
     * (true) or not (false).
     */
    @NotNull
    private boolean read;

    /**
     * Attribute that represents the associated vulnerability.
     */
    @DBRef
    private Vulnerability vulnerability;

    /**
     * Attribute that represents the associated tracking.
     */
    @DBRef
    private Tracking tracking;

    /**
     * Attribute that represents the associated user.
     */
    @DBRef
    private User user;

    /**
     * creationDate getter method.
     * 
     * @return the creationDate
     */
    public LocalDateTime getCreationDate() {
	return creationDate;
    }

    /**
     * creationDate setter method.
     * 
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(LocalDateTime creationDate) {
	this.creationDate = creationDate;
    }

    /**
     * read getter method.
     * 
     * @return the read
     */
    public boolean isRead() {
	return read;
    }

    /**
     * read setter method.
     * 
     * @param read the read to set
     */
    public void setRead(boolean read) {
	this.read = read;
    }

    /**
     * vulnerability getter method.
     * 
     * @return the vulnerability
     */
    public Vulnerability getVulnerability() {
	return vulnerability;
    }

    /**
     * vulnerability setter method.
     * 
     * @param vulnerability the vulnerability to set
     */
    public void setVulnerability(Vulnerability vulnerability) {
	this.vulnerability = vulnerability;
    }

    /**
     * tracking getter method.
     * @return the tracking
     */
    public Tracking getTracking() {
        return tracking;
    }

    /**
     * tracking setter method.
     * @param tracking the tracking to set
     */
    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
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
