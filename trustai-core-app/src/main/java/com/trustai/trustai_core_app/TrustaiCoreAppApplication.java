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

	public static void main(String[] args) {
		SpringApplication.run(TrustaiCoreAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = userRepository.findById(1L).orElse(null);

		if (user != null) return;
		User rootUser = new User("U1", "RANK_1", BigDecimal.ZERO);
		rootUser.setEmail("root@trustai.com");
		rootUser.addRole("USER");
		userRepository.save(rootUser);
	}
}
