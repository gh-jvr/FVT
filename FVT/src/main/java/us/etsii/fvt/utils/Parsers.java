package us.etsii.fvt.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.etsii.fvt.domains.Vulnerability;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.types.Reference;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the methods set that parser values.
 */
public class Parsers {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(Parsers.class);

    /**
     * Method that parses a JSON object into a Vulnerability object.
     * 
     * @param json          JSON object to parse.
     * @param startPointRef Reference of the last vulnerability stored in database.
     * @param year          Number that indicates the list year to parse.
     * @return the list of vulnerabilities contained into the JSON object or null in
     *         case of error.
     */
    public static List<Vulnerability> parseVulnerabilitiesJson(JSONObject json, Integer startPointRef, Integer year) {
	try {
	    // Si el objeto es nulo, lanzamos excepción.
	    if (json == null) {
		throw new NullPointerException(
			I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_JSON_PARSER_NULL, false));
	    }

	    // Iniciamos la lista donde almacenaremos las vulnerabilidades ya parseadas.
	    List<Vulnerability> vuls = new ArrayList<Vulnerability>();

	    // Recorremos la lista de vulnerabilidades del JSON.
	    JSONArray vulnerabilities = (JSONArray) json.get(CommonsResources.parsers.VULNERABILITY);
	    for (Object vul : vulnerabilities) {
		try {

		    Vulnerability vulEntity = new Vulnerability();
		    JSONObject vulnerability = (JSONObject) vul;
		    // Recuperamos el número de secuencia de la vulnerabilidad, el año y comprobamos
		    // si ya esta almacenada, en ese caso, no es necesario volver a parsearla y
		    // almacenarla.
		    vulEntity.setName(vulnerability.getString(CommonsResources.parsers.CVE));
		    Integer vulYear = Integer.valueOf(vulEntity.getName().split(CommonsResources.HYPHEN)[1]);
		    Integer seq = Integer.valueOf(String.valueOf(vulnerability.get(CommonsResources.parsers.ORDINAL)));
		    if (startPointRef != null && seq <= startPointRef && vulYear <= year) {
			continue;
		    }

		    // Si la vulnerabilidad no esta almacenada, la parseamos y la añadimos a la
		    // lista a guardar.
		    vulEntity.setDescription(getDescription(vulnerability));
		    vulEntity.setSeq(seq);
		    if (vulnerability.has(CommonsResources.parsers.REFERENCES) && !vulnerability
			    .get(CommonsResources.parsers.REFERENCES).equals(CommonsResources.EMPTY_STRING)) {
			List<Reference> refs = getReferences(
				(JSONObject) vulnerability.get(CommonsResources.parsers.REFERENCES));
			vulEntity.setReferences(refs);
		    }
		    vuls.add(vulEntity);
		} catch (JSONException e) {
		    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_JSON_PARSER_KEY_NOT_FOUND, true,
			    new Object[] { e }));
		}
	    }
	    return vuls;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Auxiliary method that gets the description of the vulnerability.
     * 
     * @param object JSONObject which contains the description.
     * @return a string with the description of the vulnerability or null if an
     *         error occur.
     */
    private static String getDescription(Object object) {
	try {
	    Object json = ((JSONObject) object).get(CommonsResources.parsers.NOTES);
	    if (json instanceof JSONArray) {
		return ((JSONObject) (JSONObject) ((JSONArray) ((JSONObject) json).get(CommonsResources.parsers.NOTE))
			.get(0)).getString(CommonsResources.parsers.CONTENT);
	    } else {
		Object o = ((JSONObject) json).get(CommonsResources.parsers.NOTE);
		if (o instanceof JSONArray) {
		    return ((JSONObject) ((JSONArray) o).get(0)).getString(CommonsResources.parsers.CONTENT);
		} else {
		    return ((JSONObject) o).getString(CommonsResources.parsers.CONTENT);
		}
	    }
	} catch (ClassCastException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_JSON_CAST_EXCEPTION, true,
		    new Object[] { object, e }));
	    return null;
	} catch (JSONException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_JSON_PARSER_KEY_NOT_FOUND, true,
		    new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

    /**
     * Auxiliary method that gets the list of references of a given vulnerability.
     * 
     * @param object JSONObject which contains the list of references.
     * @return a list of references or null in error case.
     */
    private static List<Reference> getReferences(Object object) {
	try {

	    // Iniciamos las variables necesarias para el parseo.
	    JSONObject json = (JSONObject) object;
	    List<Reference> refs = new ArrayList<Reference>();

	    // Si hay más de una referencia, parseamos el objeto como un array json, sino,
	    // como un objeto json.
	    if (json.get(CommonsResources.parsers.REFERENCE) instanceof JSONArray) {
		for (Object o : json.getJSONArray(CommonsResources.parsers.REFERENCE)) {
		    JSONObject jo = (JSONObject) o;
		    Reference ref = new Reference();
		    ref.setUrl(String.valueOf(jo.get(CommonsResources.parsers.URL)));
		    ref.setDescription(String.valueOf(jo.get(CommonsResources.parsers.DESCRIPTION)));
		    refs.add(ref);
		}
	    } else {
		JSONObject jo = json.getJSONObject(CommonsResources.parsers.REFERENCE);
		Reference ref = new Reference();
		ref.setUrl(String.valueOf(jo.get(CommonsResources.parsers.URL)));
		ref.setDescription(String.valueOf(jo.get(CommonsResources.parsers.DESCRIPTION)));
		refs.add(ref);
	    }
	    return refs;
	} catch (ClassCastException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_JSON_CAST_EXCEPTION, true,
		    new Object[] { object, e }));
	    return null;
	} catch (JSONException e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_JSON_PARSER_KEY_NOT_FOUND, true,
		    new Object[] { e }));
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}

    }

    /**
     * Method that parses an integer list to a string list.
     * 
     * @param listInteger Integer list to parse.
     * @return a new list with the string values of the integer, or null if the
     *         input is null.
     */
    public static List<String> parseListToString(List<Integer> listInteger) {
	try {
	    if (listInteger != null) {
		List<String> res = new ArrayList<String>();
		for (Integer i : listInteger) {
		    res.add(i.toString());
		}
		return res;
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }

}
