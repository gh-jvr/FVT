package us.etsii.fvt.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import us.etsii.fvt.utils.constants.CommonsResources;
import us.etsii.fvt.utils.constants.PathResources;

/**
 * 
 * @author Javier Villalba Ramírez Free Vulnerabilities Tracker, 2019
 * 
 *         This class takes care of the general web configuration. It manages
 *         the language of the application, the password encryption and help to
 *         the build of some views.
 */
@Configuration
public class PageConfig implements WebMvcConfigurer {

    /**
     * Method used to encode the passwords.
     * 
     * @return a password encoded object.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	return bCryptPasswordEncoder;
    }

    /**
     * Method that setups the language by default.
     * 
     * @return a localeResolver object, which represents the current language.
     */
    @Bean
    public LocaleResolver localeResolver() {
	SessionLocaleResolver slr = new SessionLocaleResolver();
	slr.setDefaultLocale(Locale.US);
	return slr;
    }

    /**
     * Method in charges of changing the language when the user required it.
     * 
     * @return a object with the new language.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
	LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	lci.setParamName(CommonsResources.LANG_PARAM);
	return lci;
    }

    /**
     * Method that manages automatically the controllers of the some simples views.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	registry.addViewController(CommonsResources.SLASH + PathResources.HOME).setViewName(PathResources.HOME);
	registry.addViewController(CommonsResources.SLASH).setViewName(PathResources.HOME);
	registry.addViewController(CommonsResources.SLASH + PathResources.LOG_IN).setViewName(PathResources.LOG_IN);
    }

    /**
     * Method that adds a new language interceptor to the registry.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Method that manages the location of the messages files.
     * 
     * @return the place where the messages files are.
     */
    @Bean
    public MessageSource messageSource() {
	ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	messageSource.setBasename(PathResources.STATIC + CommonsResources.SLASH + PathResources.MESSAGES
		+ CommonsResources.SLASH + PathResources.MESSAGES);
	messageSource.setDefaultEncoding(CommonsResources.UTF8);
	return messageSource;
    }

    /**
     * Method that sets the default validation messages file location.
     * 
     * @return a bean factory with the new file which contains the messages
     *         validation.
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
	LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
	bean.setValidationMessageSource(messageSource());
	return bean;
    }

    /**
     * Method that helps to Spring and Thymeleaf to transform date objects into Java
     * 8 date objects.
     * 
     * @param templateResolver template to process.
     * @return the new spring template.
     */
    @Bean
    public ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
	SpringTemplateEngine engine = new SpringTemplateEngine();
	engine.addDialect(new Java8TimeDialect());
	engine.setTemplateResolver(templateResolver);
	return engine;
    }
}
