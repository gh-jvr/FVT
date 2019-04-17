package us.etsii.fvt.domains;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;

/**
 * 
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the alarm entity.
 */
@Document(collection = Parameters.Collections.ALARMS)
public class Alarm extends Entity implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2234941166370755998L;

    /**
     * Alarm name attribute. It represents the name of the alarm.
     */
    @NotBlank(message = LanguageKeys.SpringValidationMessages.ERROR_ALARM_NAME_BLANK)
    @Indexed(direction = IndexDirection.DESCENDING)
    private String alarmName;

    /**
     * Alarm period attribute. It represents the hours, minutes and seconds for the
     * alarm interval.
     */
    @NotNull(message = LanguageKeys.SpringValidationMessages.ERROR_ALARM_PERIOD_BLANK)
    private Integer alarmPeriod;

    /**
     * enabled attribute. It represents if the alarms is enabled (true) or disabled
     * (false).
     */
    private boolean enabled;

    /**
     * email notification attribute. It represents if the system must sent a email
     * when the alarm is produced.
     */
    private boolean emailNotification;

    /**
     * alarmName getter method.
     * 
     * @return the alarmName
     */
    public String getAlarmName() {
	return alarmName;
    }

    /**
     * alarmName setter method.
     * 
     * @param alarmName the alarmName to set
     */
    public void setAlarmName(String alarmName) {
	this.alarmName = alarmName;
    }

    /**
     * alarmPeriod getter method.
     * 
     * @return the alarmPeriod
     */
    public Integer getAlarmPeriod() {
	return alarmPeriod;
    }

    /**
     * alarmPeriod setter method.
     * 
     * @param alarmPeriod the alarmPeriod to set
     */
    public void setAlarmPeriod(Integer alarmPeriod) {
	this.alarmPeriod = alarmPeriod;
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
     * emailNotification getter method.
     * 
     * @return the emailNotification
     */
    public boolean isEmailNotification() {
	return emailNotification;
    }

    /**
     * emailNotification setter method.
     * 
     * @param emailNotification the emailNotification to set
     */
    public void setEmailNotification(boolean emailNotification) {
	this.emailNotification = emailNotification;
    }

}
