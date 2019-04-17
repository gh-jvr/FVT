package us.etsii.fvt.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.PropertiesNames;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents a methods set that helps to the CVE
 *         synchronization task.
 */
public class CveUtils {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(CveUtils.class);

    /**
     * Constant that represents the base URL of the CVE website.
     */
    private static final String CVE_URL_ROOT = Io.getCveUrl();

    /**
     * Method that get the xml file from CVE website with the vulnerabilities.
     * 
     * @param year Year of the vulnerabilities list.
     * 
     * @return a string which represents the XML file.
     * @throws ClientProtocolException if something wrong happened in the HTTP
     *                                 communication.
     * @throws IOException             if something wrong happened in the input and
     *                                 output buffer.
     */
    public String getXmlStringVulnerabilitiesList(String year) throws ClientProtocolException, IOException {
	// Creamos una instancia del objecto que nos permitirá realizar la
	// petición http.
	CloseableHttpClient httpClient = HttpClients.createDefault();
	// Obtenemos el recurso necesario para el año solicitado
	if (Io.getCveResource(PropertiesNames.CVE_RESOURCES_BY_YEAR_PREFIX + year) == null
		|| Io.getCveResource(PropertiesNames.CVE_RESOURCES_BY_YEAR_PREFIX + year).isEmpty()) {
	    return null;
	}
	String url = CVE_URL_ROOT + Io.getCveResource(PropertiesNames.CVE_RESOURCES_BY_YEAR_PREFIX + year);
	try {
	    // Creamos un método GET con la URL definida en los ficheros de
	    // propiedades.
	    HttpGet httpget = new HttpGet(url);
	    // Creamos el objeto encargado de recibir la respuesta del servidor.
	    ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		// Creamos el gestor de la respuesta
		@Override
		public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
		    // Obtenemos el código de estado de la respuesta
		    int status = response.getStatusLine().getStatusCode();
		    // Si el código es correcto, devolvemos la respuesta
		    // recibida. Si se ha producido algún error, lo registramos
		    // en el log y lanzamos una excepción.
		    if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? EntityUtils.toString(entity) : null;
		    } else {
			LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_STATUS_HTTP_NOT_VALID, true,
				new Object[] { status, url }));
			throw new ClientProtocolException();
		    }
		}
	    };
	    // Devolvemos el resultado de la petición
	    return httpClient.execute(httpget, responseHandler);
	} finally {
	    // Cerramos la conexión con el servidor
	    httpClient.close();
	}
    }

    /**
     * Method that get the vulnerabilities list in JSON format.
     * 
     * @param year Year of the vulnerabilities list.
     * @return a JSONObject with the vulnerabilities list.
     * @throws JSONException           if the conversion process fails.
     * @throws ClientProtocolException if the process
     * @throws IOException             if an error occurred in the input/output task.
     */
    public JSONObject getJsonVulnerabilitiesList(String year)
	    throws JSONException, ClientProtocolException, IOException {
	String xml = getXmlStringVulnerabilitiesList(year);
	if (xml != null) {
	    xml = xml.substring(xml.indexOf(CommonsResources.parsers.INITIAL_XML_DELIMITED),
		    xml.lastIndexOf(CommonsResources.parsers.FINAL_XML_DELIMITED));
	    JSONObject json = XML.toJSONObject(xml);
	    return json;
	} else {
	    throw new IOException();
	}
    }

    /**
     * Method that gets from the static properties file all year defined for the CVE
     * URL.
     * 
     * @return a sorted list with the available years or null in error case.
     */
    public List<Integer> getAllAvailableYear() {
	try {
	    // buscamos todos los elementos del fichero de propiedades que coincidan con el
	    // patrón 'res_year_'.
	    List<Integer> res = new ArrayList<>();
	    List<String> props = Io.searchSetProperties(
		    PropertiesNames.CVE_RESOURCES_BY_YEAR_PREFIX + CommonsResources.ONLY_NUMBER_PATTERN_2);
	    for (String prop : props) {
		if (prop.contains(PropertiesNames.CVE_RESOURCES_BY_YEAR_PREFIX)) {
		    try {
			prop = prop.replaceAll(PropertiesNames.CVE_RESOURCES_BY_YEAR_PREFIX,
				CommonsResources.EMPTY_STRING);
			res.add(Integer.valueOf(prop));
		    } catch (ClassCastException e) {
			LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_PARSE_INTEGER, true,
				new Object[] { prop }));
		    }
		}
	    }
	    Collections.sort(res);
	    return res;
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	    return null;
	}
    }
}
