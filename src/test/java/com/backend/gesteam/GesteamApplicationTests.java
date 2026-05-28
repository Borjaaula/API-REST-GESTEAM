package com.backend.gesteam;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = {
				"spring.jpa.hibernate.ddl-auto=none",
				"spring.flyway.enabled=false"
		}
)
class GesteamApplicationTests {

	@Test
	void contextLoads() {
	}

}
