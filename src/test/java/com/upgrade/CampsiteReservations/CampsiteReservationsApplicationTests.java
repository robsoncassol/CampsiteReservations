package com.upgrade.CampsiteReservations;

import com.upgrade.CampsiteReservations.config.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestRedisConfiguration.class})
class CampsiteReservationsApplicationTests {

	@Test
	void contextLoads() {
	}

}
