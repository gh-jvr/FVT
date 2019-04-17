package us.etsii.fvt.utils;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.InvalidAttributeValueException;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.etsii.fvt.domains.Notification;
import us.etsii.fvt.domains.Setting;
import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.LanguageKeys;
import us.etsii.fvt.utils.constants.Parameters;
import us.etsii.fvt.utils.types.Reference;

/**
 * @author Javier Villalba Ramírez. Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the auxiliary set of method of notifications.
 */
public class NotificationUtils {

    /**
     * Logger constant used to write in the log of the application.
     */
    private static final Logger LOGGER = LogManager.getLogger(NotificationUtils.class);

    /**
     * Constant that represents the SMTP host name;
     */
    private static final String SMTP_HOST = Io.getSmtpHost();

    /**
     * Constant that represents if the SMTP connection needs authentication.
     */
    private static final String SMTP_AUTH = Io.getSmtpAuthentication();

    /**
     * Constant that represents the SMTP port.
     */
    private static final String SMTP_PORT = Io.getSmtpPort();

    /**
     * Constant that represents the SSL SMTP port.
     */
    private static final String SMTP_SSL_PORT = Io.getSmtpSslPort();

    /**
     * Constant that represents if the socket factory fallback is enabled or not.
     */
    private static final String SMTP_FALLBACK = Io.getSmtpSocketFactoryFallback();

    /**
     * Constant that represents the sender email.
     */
    private static final String SMTP_FROM = Io.getSmtpFrom();

    /**
     * Constant that represents the sender email password.
     */
    private static final String SMTP_PSWD = Io.getSmtpPassword();

    /**
     * Auxiliary static method that send an email to notify a vulnerability.
     * 
     * @param not     Notification with the necessary information of the issue.
     * @param setting User setting with the email information.
     */
    public static void sendEmail(Notification not, Setting setting) {
	try {

	    // Comprobamos que los parámetros recibidos son válidos para enviar el correo.
	    if (not == null || setting == null) {
		throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_NULL_PARAMS_EMAIL, true));
	    }
	    if (setting.getEmail() == null || setting.getEmail().isEmpty()) {
		throw new InvalidAttributeValueException(I18n.getResource(LanguageKeys.ERROR_EMAIL_NOT_VALID, true));
	    }

	    // Recuperamos la lista de propiedades necesarias para enviar el correo.
	    Properties props = new Properties();
	    props.put(Parameters.SMTP_HOST, SMTP_HOST);
	    props.setProperty(Parameters.MAIL_TRANSPORT_PROTOCOL, CommonsResources.SMTP);
	    props.put(Parameters.SMTP_AUTH, SMTP_AUTH);
	    props.put(Parameters.SMTP_PORT, SMTP_PORT);
	    props.put(Parameters.SMTP_SOCKET_FACTORY_PORT, SMTP_SSL_PORT);
	    props.put(Parameters.SMTP_SOCKET_FACTORY_CLASS, CommonsResources.SMTP_SSL_SOCKET_CLASS);
	    props.put(Parameters.SMTP_SOCKET_FACTORY_FALLBACK, SMTP_FALLBACK);
	    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
		    return new PasswordAuthentication(SMTP_FROM, SMTP_PSWD);
		}
	    });

	    // iniciamos las variables necesarias para enviar el correo.
	    Transport transport = session.getTransport();
	    MimeMessage mime = new MimeMessage(session);

	    // Insertamos la información necesaria para el envio.
	    mime.setFrom(new InternetAddress(SMTP_FROM));
	    mime.addRecipient(Message.RecipientType.TO, new InternetAddress(setting.getEmail()));
	    mime.setSubject(not.getVulnerability().getName());
	    String msg = generateMessageBody(not);
	    if (msg == null) {
		throw new NullArgumentException(I18n.getResource(LanguageKeys.ERROR_NULL_PARAM_MESSAGE, true));
	    }
	    mime.setText(msg);

	    // Enviamos el correo.
	    transport.connect();
	    Transport.send(mime);
	    transport.close();
	    LOGGER.debug(I18n.getResource(LanguageKeys.LogsMessages.LOG_EMAIL_SENT, true,
		    new Object[] { setting.getEmail(), msg }));

	} catch (SendFailedException e) {
	    LOGGER.warn(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_EMAIL_FAILURE_SEND, true, new Object[] { e }));
	} catch (AuthenticationFailedException e) {
	    LOGGER.warn(
		    I18n.getResource(LanguageKeys.LogsMessages.LOG_ERROR_EMAIL_FAILURE_AUTH, true, new Object[] { e }));
	} catch (NullArgumentException | InvalidAttributeValueException | MessagingException e) {
	    LOGGER.warn(I18n.getResource(LanguageKeys.LogsMessages.LOG_EMAIL_NOT_SENT, true, new Object[] { e }));
	} catch (Exception e) {
	    LOGGER.error(I18n.getResource(LanguageKeys.LogsMessages.UNEXPECTED_ERROR, true, new Object[] { e }));
	}
    }

    /**
     * Auxiliary method that generates the string content of the email.
     * 
     * @param not Notification object which contains all the information.
     * @return a string which represents the body of the email.
     */
    private static String generateMessageBody(Notification not) {
	if (not != null) {
	    // Creamos el stringBuilder donde iremos construyendo el mensaje.
	    StringBuilder sb = new StringBuilder();

	    // Añadimos el mensaje inicial del correo.
	    sb.append(I18n.getResource(LanguageKeys.EMAIL_BODY_START, true,
		    new Object[] { not.getTracking().getTrackingName(), not.getTracking().getSoftwareName() }));
	    sb.append(CommonsResources.NEW_LINE);
	    sb.append(CommonsResources.NEW_LINE);

	    // Añadimos la descripción de la vulnerabilidad CVE.
	    sb.append(I18n.getResource(LanguageKeys.EMAIL_BODY_CVE_DESCRIPTION, true));
	    sb.append(CommonsResources.NEW_LINE);
	    sb.append(not.getVulnerability().getDescription());

	    // Si la vulnerabilidad tiene referencias, las añadimos.
	    if (not.getVulnerability().getReferences() != null) {
		sb.append(CommonsResources.NEW_LINE);
		sb.append(CommonsResources.NEW_LINE);
		sb.append(I18n.getResource(LanguageKeys.EMAIL_BODY_REFS, true,
			new Object[] { not.getVulnerability().getReferences().size() }));
		if (!not.getVulnerability().getReferences().isEmpty()) {
		    sb.append(CommonsResources.NEW_LINE);
		    for (Reference ref : not.getVulnerability().getReferences()) {
			sb.append(ref.getDescription());
			sb.append(CommonsResources.NEW_LINE);
			sb.append(ref.getUrl());
			sb.append(CommonsResources.NEW_LINE);
			sb.append(CommonsResources.NEW_LINE);
		    }
		}
		sb.append(CommonsResources.NEW_LINE);
		sb.append(CommonsResources.NEW_LINE);
	    }

	    // Añadimos el mensaje final para que no se conteste al correo.
	    sb.append(CommonsResources.NEW_LINE);
	    sb.append(CommonsResources.NEW_LINE);
	    sb.append(I18n.getResource(LanguageKeys.EMAIL_NOT_REPLY, true));

	    // Devolvemos el mensaje completo como un string.
	    return sb.toString();
	}
	return null;
    }

}
