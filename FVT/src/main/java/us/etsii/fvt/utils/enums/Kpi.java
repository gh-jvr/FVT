package us.etsii.fvt.utils.enums;

import us.etsii.fvt.utils.constants.Enums;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This enumerate contains the set of possible KPIs that can be showed
 *         in the Dashboard view.
 */
public enum Kpi {
    /**
     * Vulnerabilities list related with some tracking.
     */
    KPI_1(Enums.KPI_1),

    /**
     * Notification total number.
     */
    KPI_2(Enums.KPI_2),

    /**
     * Unread notification total number.
     */
    KPI_3(Enums.KPI_3),

    /**
     * Tracking total number.
     */
    KPI_4(Enums.KPI_4),

    /**
     * Alarm total number.
     */
    KPI_5(Enums.KPI_5),

    /**
     * Vulnerable trackings list.
     */
    KPI_6(Enums.KPI_6),

    /**
     * Notification number of each tracking.
     */
    KPI_7(Enums.KPI_7),

    /**
     * vulnerabilities number of each tracking.
     */
    KPI_8(Enums.KPI_8);

    /**
     * Attribute that represent the name of the KPI.
     */
    private String value;

    /**
     * One parameter constructor.
     * 
     * @param value Name of the KPI.
     */
    Kpi(String value) {
	this.value = value;
    }

    /**
     * value getter method.
     * 
     * @return the value
     */
    public String getValue() {
	return value;
    }

    /**
     * value setter method.
     * 
     * @param value the value to set
     */
    public void setValue(String value) {
	this.value = value;
    }

}
