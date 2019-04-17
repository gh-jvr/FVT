package us.etsii.fvt.domains;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import us.etsii.fvt.utils.constants.Parameters;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the dashboard entity.
 */
@Document(collection = Parameters.Collections.DASHBOARDS)
public class Dashboard extends Entity implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 9164270485382373432L;

    /**
     * Attribute that represents if the KPI7 should be printed (true) or not
     * (false).
     */
    @NotNull
    private boolean kpi7;

    /**
     * Attribute that represents if the KPI8 should be printed (true) or not
     * (false).
     */
    @NotNull
    private boolean kpi8;

    /**
     * Chart option chose for the KPI 7.
     */
    @NotBlank
    private String chartOptionKpi7;

    /**
     * Chart option chose for the KPI 8.
     */
    @NotBlank
    private String chartOptionKpi8;

    /**
     * User relational attribute. It represents the user which belongs this
     * dashboard object.
     */
    @DBRef
    private User user;

    /**
     * kpi7 getter method.
     * 
     * @return the kpi7
     */
    public boolean isKpi7() {
	return kpi7;
    }

    /**
     * kpi7 setter method.
     * 
     * @param kpi7 the kpi7 to set
     */
    public void setKpi7(boolean kpi7) {
	this.kpi7 = kpi7;
    }

    /**
     * kpi8 getter method.
     * 
     * @return the kpi8
     */
    public boolean isKpi8() {
	return kpi8;
    }

    /**
     * kpi8 setter method.
     * 
     * @param kpi8 the kpi8 to set
     */
    public void setKpi8(boolean kpi8) {
	this.kpi8 = kpi8;
    }

    /**
     * chartOptionKpi7 getter method.
     * 
     * @return the chartOptionKpi7
     */
    public String getChartOptionKpi7() {
	return chartOptionKpi7;
    }

    /**
     * chartOptionKpi7 setter method.
     * 
     * @param chartOptionKpi7 the chartOptionKpi7 to set
     */
    public void setChartOptionKpi7(String chartOptionKpi7) {
	this.chartOptionKpi7 = chartOptionKpi7;
    }

    /**
     * chartOptionKpi8 getter method.
     * 
     * @return the chartOptionKpi8
     */
    public String getChartOptionKpi8() {
	return chartOptionKpi8;
    }

    /**
     * chartOptionKpi8 setter method.
     * 
     * @param chartOptionKpi8 the chartOptionKpi8 to set
     */
    public void setChartOptionKpi8(String chartOptionKpi8) {
	this.chartOptionKpi8 = chartOptionKpi8;
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
