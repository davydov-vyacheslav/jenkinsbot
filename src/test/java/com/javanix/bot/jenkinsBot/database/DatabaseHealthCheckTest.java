package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseHealthCheckTest extends AbstractDatabaseEntityTest {

	@Autowired
	HealthCheckService databaseService;

	@Test
	@Override
	public void testGetOwnedEntities() {
		List<HealthCheckInfoDto> ownedEntities = databaseService.getOwnedEntities(CURRENT_USER_ID).collect(Collectors.toList());
		assertEquals(3, ownedEntities.size());
		assertThat(Arrays.asList("owned-public", "owned-private", "owned-public-ref"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@Override
	@Test
	void testGetAvailableEntitiesToReference() {
		List<HealthCheckInfoDto> ownedEntities = databaseService.getAvailableEntitiesToReference(CURRENT_USER_ID).collect(Collectors.toList());
		assertEquals(2, ownedEntities.size());
		assertThat(Arrays.asList("foreign-public", "foreign-public-ref2"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@Override
	@Test
	void testGetOwnedOrReferencedEntities() {
		List<HealthCheckInfoDto> availableEntities = databaseService.getOwnedOrReferencedEntities(CURRENT_USER_ID).collect(Collectors.toList());
		assertEquals(4, availableEntities.size());
		assertThat(Arrays.asList("owned-public", "owned-public-ref", "owned-private", "foreign-private-ref"))
				.containsExactlyInAnyOrderElementsOf(availableEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@BeforeEach
	public void dataSetup() {
		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.endpointName("owned-public")
				.isPublic(true).creatorId(CURRENT_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.endpointName("foreign-public")
				.isPublic(true).creatorId(SOMEONE_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.endpointName("owned-private")
				.isPublic(false).creatorId(CURRENT_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.endpointName("foreign-private")
				.isPublic(false).creatorId(SOMEONE_USER_ID).build());

		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.endpointName("owned-public-ref").isPublic(true)
				.referencedByUsers(new HashSet<>(Arrays.asList(SOMEONE_USER_ID, 999L)))
				.creatorId(CURRENT_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.referencedByUsers(new HashSet<>(Arrays.asList(CURRENT_USER_ID, 999L)))
				.endpointName("foreign-private-ref").isPublic(false).creatorId(SOMEONE_USER_ID).build());
		databaseService.save(HealthCheckInfoDto.emptyEntityBuilder()
				.referencedByUsers(new HashSet<>(Arrays.asList(888L, 999L)))
				.endpointName("foreign-public-ref2").isPublic(true).creatorId(SOMEONE_USER_ID).build());
	}

}
