package us.etsii.fvt.utils.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This enumerate contains the set of possible alarm period value
 *         expressed in hours.
 */
public enum Period {

    /**
     * One hour.
     */
    ONE_HOUR(1), 
    
    /**
     * Six hours.
     */
    SIX_HOURS(6), 
    
    /**
     * One day.
     */
    ONE_DAY(24), 
    
    /**
     * One week.
     */
    ONE_WEEK(168), 
    
    /**
     * One month.
     */
    ONE_MONTH(720), 
    
    /**
     * Six months.
     */
    SIX_MONTHS(4320);

    /**
     * Attribute that represents the value of the enumerate.
     */
    private Integer value;

    /**
     * Constructor with one parameter.
     * 
     * @param hours number that represents the enumerate.
     */
    Period(Integer value) {
	this.value = value;
    }

    /**
     * value getter method.
     * 
     * @return the value
     */
    public Integer getValue() {
	return value;
    }

    /**
     * value setter method.
     * 
     * @param value the value to set
     */
    public void setValue(Integer value) {
	this.value = value;
    }

    /**
     * Method that gets all the available periods names.
     * 
     * @return an array string with the available names.
     */
    public static List<Integer> getAllValues() {
	List<Integer> res = new ArrayList<Integer>();
	for (Period period : Period.values()) {
	    res.add(period.value);
	}
	return res;
    }

}
