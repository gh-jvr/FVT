package us.etsii.fvt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import us.etsii.fvt.domains.Role;
import us.etsii.fvt.repositories.RoleRepository;
import us.etsii.fvt.utils.constants.Roles;

/**
 * 
 * @author Javier Villalba Ramírez Free Vulnerabilities Tracker, 2019
 * 
 *         This class represents the main method of the project, which start the
 *         application execution.
 */
@SpringBootApplication
@EnableScheduling
public class FvtApplication {

    /**
     * Main method that run the application.
     * 
     * @param args Arguments.
     */
    public static void main(String[] args) {
	// Iniciamos la plataforma
	SpringApplication.run(FvtApplication.class, args);
    }

    /**
     * Method that initializes the roles of the repository.
     * 
     * @param roleRepository
     * @return the roles available: 'Admin' and 'User'.
     */
    @Bean
    CommandLineRunner init(RoleRepository roleRepository) {
	return args -> {
	    Role adminRole = roleRepository.findByRole(Roles.ADMIN);
	    if (adminRole == null) {
		Role newAdminRole = new Role();
		newAdminRole.setRole(Roles.ADMIN);
		roleRepository.save(newAdminRole);
	    }

	    Role userRole = roleRepository.findByRole(Roles.USER);
	    if (userRole == null) {
		Role newUserRole = new Role();
		newUserRole.setRole(Roles.USER);
		roleRepository.save(newUserRole);
	    }
	};
    }

}
