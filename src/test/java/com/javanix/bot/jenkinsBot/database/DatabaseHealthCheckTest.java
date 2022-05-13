package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseHealthCheckTest extends AbstractDatabaseEntityTest {

	@Autowired
	HealthCheckService databaseService;

	@Test
	public void testGetAvailableEndpoints() {
		Collection<HealthCheckInfoDto> availableEndpoints = databaseService.getAvailableEndpoints(CURRENT_USER_ID);
		assertEquals(3, availableEndpoints.size());
		assertThat(Arrays.asList("owned-public", "foreign-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(availableEndpoints.stream().map(HealthCheckInfoDto::getEndpointName)
						.collect(Collectors.toList()));
	}

	@Test
	public void testGetOwnedEntities() {
		List<HealthCheckInfoDto> ownedEntities = databaseService.getOwnedEntities(CURRENT_USER_ID);
		assertEquals(2, ownedEntities.size());
		assertThat(Arrays.asList("owned-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@BeforeEach
	public void dataSetup() {
		databaseService.save(HealthCheckInfoDto.builder()
				.endpointName("owned-public")
				.isPublic(true).creatorId(CURRENT_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.builder()
				.endpointName("foreign-public")
				.isPublic(true).creatorId(SOMEONE_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.builder()
				.endpointName("owned-private")
				.isPublic(false).creatorId(CURRENT_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.builder()
				.endpointName("foreign-private")
				.isPublic(false).creatorId(SOMEONE_USER_ID).build());
	}

}
