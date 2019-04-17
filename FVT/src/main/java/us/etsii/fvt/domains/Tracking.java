package us.etsii.fvt.domains;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the Tracking entity.
 */
@Document(collection = Parameters.Collections.TRACKINGS)
public class Tracking extends Entity implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -1249902722123443448L;

    /**
     * Tracking name attribute. It represents the name of the tracking.
     */
    @NotBlank(message = LanguageKeys.SpringValidationMessages.ERROR_TRACKING_NAME_BLANK)
    @Indexed(direction = IndexDirection.DESCENDING)
    private String trackingName;

    /**
     * Software name attribute. It represents the full name of the software to
     * follow. It must match with the official name.
     */
    @NotBlank(message = LanguageKeys.SpringValidationMessages.ERROR_SOFTWARE_NAME_BLANK)
    private String softwareName;

    /**
     * Alarms relational attribute. It represents the list of alarms related with
     * this tracking.
     */
    @DBRef
    private Set<Alarm> alarms;

    /**
     * User relational attribute. It represents the user which belongs this tracking.
     */
    @DBRef
    private User user;

    /**
     * trackingName getter method.
     * @return the trackingName
     */
    public String getTrackingName() {
        return trackingName;
    }

    /**
     * trackingName setter method.
     * @param trackingName the trackingName to set
     */
    public void setTrackingName(String trackingName) {
        this.trackingName = trackingName;
    }

    /**
     * SoftwareName getter method.
     * @return the softwareName
     */
    public String getSoftwareName() {
        return softwareName;
    }

    /**
     * SoftwareName setter method.
     * @param softwareName the softwareName to set
     */
    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    /**
     * alarms getter method.
     * @return the alarms
     */
    public Set<Alarm> getAlarms() {
        return alarms;
    }

    /**
     * alarms setter method.
     * @param alarms the alarms to set
     */
    public void setAlarms(Set<Alarm> alarms) {
        this.alarms = alarms;
    }

    /**
     * user getter method.
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * user setter method.
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}
