package us.etsii.fvt.utils.enums;

import us.etsii.fvt.utils.constants.Enums;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This enumerate contains the set of possible chart that can be showed
 *         in the Dashboard view.
 */
public enum Chart {

    /**
     * Lines chart.
     */
    LINE(Enums.LINE),

    /**
     * Bars chart.
     */
    BAR(Enums.BAR),

    /**
     * Pie chart.
     */
    PIE(Enums.PIE);

    /**
     * Attribute that represents the chart name.
     */
    private String value;

    /**
     * One parameter constructor.
     * 
     * @param value Chart name.
     */
    Chart(String value) {
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
