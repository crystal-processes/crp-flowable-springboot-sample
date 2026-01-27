package org.crp.flowable.springboot.sample;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AcmeApplication {

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
                identityService.createMembership("jlong", "user");
            }

            // Create demo user: admin with password "test"
            if (identityService.createUserQuery().userId("admin").count() == 0L) {
                User admin = identityService.newUser("admin");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                // Store encoded password for JWT authentication
                admin.setPassword("test");
                identityService.saveUser(admin);
                identityService.createMembership("admin", "user");
            }

            // Create demo user: test with password "test"
            if (identityService.createUserQuery().userId("test").count() == 0L) {
                User testUser = identityService.newUser("test");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                // Store encoded password for JWT authentication
                testUser.setPassword("test");
                identityService.saveUser(testUser);
                identityService.createMembership("test", "user");
            }

        };
	}

	public static void main(String[] args) {
		SpringApplication.run(AcmeApplication.class, args);
	}

}
