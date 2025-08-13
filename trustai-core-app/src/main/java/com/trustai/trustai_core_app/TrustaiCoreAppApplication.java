package com.trustai.trustai_core_app;

import com.trustai.common_base.domain.user.Role;
import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.user.entity.Kyc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootApplication
@ComponentScan(basePackages = {"com.trustai.*"})
public class TrustaiCoreAppApplication implements CommandLineRunner {
	@Autowired
	UserRepository userRepository;

	String adminPassword = "$2a$12$7Q.ejHVLMMtPyBu4VQEULO8TnoJlX1xSuzjLn07GjLmeNwuJrt8Yy"; // admin@123
	String testPassword1 = "$2a$12$4sLp.sNltnRtf7tsDD4m1Oz1LxRY2MmNTZqe8bWnmt7/3lRW68ILq"; // test1
	String testPassword2 = "$2a$12$yXQ9cmGqwXJav5utBBHg3uG.fxV0.fRISbg6mNx5lH.nh1PEuAFPi"; // test2

	public static void main(String[] args) {
		SpringApplication.run(TrustaiCoreAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = userRepository.findById(1L).orElse(null);

		if (user != null) return;
		/*User rootUser = new User("U1", "RANK_1", BigDecimal.ZERO);
		rootUser.setEmail("root@trustai.com");
		rootUser.addRole("USER");
		userRepository.save(rootUser);*/

		User user_admin = new User("root", "RANK_1", new BigDecimal("50000"));
		user_admin.setEmail("root@trustai.com");
		user_admin.addRole("ADMIN");
		user_admin.setPassword(adminPassword);
		userRepository.save(user_admin);

		User user1 = new User("test1", "RANK_1", new BigDecimal("50000"));
		user1.setEmail("test1@test.com");
		user1.addRole("USER");
		user1.setPassword(testPassword1);
		userRepository.save(user1);

		/*User user2 = new User("test2", "RANK_1", new BigDecimal("50000"));
		user2.setEmail("test2@gmail.com");
		user2.addRole("USER");
		user_admin.setPassword(testPassword2);
		userRepository.save(user2);*/



	}
}
