package org.crp.flowable.springboot.sample;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application class for the Acme Spring Boot application.
 * This class initializes the application and sets up default users and groups.
 */
@SpringBootApplication
public class AcmeApplication {

	/**
	 * Creates an InitializingBean that initializes default users and groups.
	 *
	 * @param identityService the Flowable identity service
	 * @return an InitializingBean that creates default users and groups
	 */
	@Bean
	InitializingBean usersAndGroupsInitializer(final IdentityService identityService) {

		return () -> {

            // install groups & users
            if (identityService.createGroupQuery().groupId("user").count() == 0L) {
                Group group = identityService.newGroup("user");
                group.setName("users");
                group.setType("security-role");
                identityService.saveGroup(group);
            }

            if (identityService.createUserQuery().userId("jlong").count() == 0L) {
                User josh = identityService.newUser("jlong");
                josh.setFirstName("Josh");
                josh.setLastName("Long");
                josh.setPassword("password");
                identityService.saveUser(josh);
            }

            if (identityService.createUserQuery().userId("jlong").memberOfGroup("user").count() == 0L) {
                identityService.createMembership("jlong", "user");
            }
        };
	}

	/**
	 * The main method that starts the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(AcmeApplication.class, args);
	}

}
