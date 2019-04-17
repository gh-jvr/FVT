package us.etsii.fvt.utils.types;

import java.io.Serializable;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the reference object type, object used to parse
 *         the vulnerabilities list.
 */
public class Reference implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7665863637450343758L;

    /**
     * Attribute that represents the description of the reference.
     */
    private String description;

    /**
     * Attribute that represents the URL of the reference.
     */
    private String url;

    /**
     * description getter method.
     * 
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * description setter method.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * url getter method.
     * 
     * @return the url
     */
    public String getUrl() {
	return url;
    }

    /**
     * url setter method.
     * 
     * @param url the url to set
     */
    public void setUrl(String url) {
	this.url = url;
    }

}
