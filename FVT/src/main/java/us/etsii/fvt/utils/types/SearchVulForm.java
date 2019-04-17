package us.etsii.fvt.utils.types;

import java.io.Serializable;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the form entity to search in the vulnerability
 *         view.
 */
public class SearchVulForm implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3759848614707821433L;

    /**
     * Attribute that represents the input text to search in the vulnerability list.
     */
    private String search;

    /**
     * Attribute that represents the year of the filter.
     */
    private String year;

    /**
     * search getter method.
     * 
     * @return the search
     */
    public String getSearch() {
	return search;
    }

    /**
     * search setter method.
     * 
     * @param search the search to set
     */
    public void setSearch(String search) {
	this.search = search;
    }

    /**
     * year getter method.
     * 
     * @return the year
     */
    public String getYear() {
	return year;
    }

    /**
     * year setter method.
     * 
     * @param year the year to set
     */
    public void setYear(String year) {
	this.year = year;
    }

}
