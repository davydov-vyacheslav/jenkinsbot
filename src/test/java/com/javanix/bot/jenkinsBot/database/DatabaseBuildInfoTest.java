package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.ConsoleOutputConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseBuildInfoTest extends AbstractDatabaseEntityTest {

	@Autowired
	ConsoleOutputConfigService consoleOutputConfigService;

	@Autowired
	BuildInfoService databaseService;

	@Test
	void testRemoveEntity() {
		Map<String, BuildInfoDto> availableRepositories = databaseService.getOwnedOrReferencedEntities(CURRENT_USER_ID)
				.collect(Collectors.toMap(BuildInfoDto::getRepoName, Function.identity()));

		assertTrue(availableRepositories.containsKey("owned-public"));
		assertTrue(availableRepositories.get("foreign-private-ref").getReferencedByUsers().contains(CURRENT_USER_ID));

		databaseService.removeEntity(CURRENT_USER_ID, "owned-public");
		databaseService.removeEntity(CURRENT_USER_ID, "foreign-private-ref");

		availableRepositories = databaseService.getOwnedOrReferencedEntities(CURRENT_USER_ID)
				.collect(Collectors.toMap(BuildInfoDto::getRepoName, Function.identity()));

		assertFalse(availableRepositories.containsKey("owned-public"));
		assertFalse(availableRepositories.containsKey("foreign-private-ref"));

		Map<String, BuildInfoDto> otherRepos = databaseService.getOwnedOrReferencedEntities(999L)
				.collect(Collectors.toMap(BuildInfoDto::getRepoName, Function.identity()));
		assertTrue(otherRepos.containsKey("foreign-private-ref"));
		assertFalse(otherRepos.get("foreign-private-ref").getReferencedByUsers().contains(CURRENT_USER_ID));
	}

	@Test
	@Override
	public void testGetOwnedEntities() {
		List<BuildInfoDto> ownedEntities = databaseService.getOwnedEntities(CURRENT_USER_ID).collect(Collectors.toList());
		assertEquals(3, ownedEntities.size());
		assertThat(Arrays.asList("owned-public", "owned-private", "owned-public-ref"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@Override
	@Test
	void testGetAvailableEntitiesToReference() {
		List<BuildInfoDto> ownedEntities = databaseService.getAvailableEntitiesToReference(CURRENT_USER_ID).collect(Collectors.toList());
		assertEquals(2, ownedEntities.size());
		assertThat(Arrays.asList("foreign-public", "foreign-public-ref2"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@Override
	@Test
	void testGetOwnedOrReferencedEntities() {
		List<BuildInfoDto> availableRepositories = databaseService.getOwnedOrReferencedEntities(CURRENT_USER_ID).collect(Collectors.toList());
		assertEquals(4, availableRepositories.size());
		assertThat(Arrays.asList("owned-public", "owned-public-ref", "owned-private", "foreign-private-ref"))
				.containsExactlyInAnyOrderElementsOf(availableRepositories.stream().map(BuildInfoDto::getRepoName).collect(Collectors.toList()));
	}

	@BeforeEach
	public void dataSetup() {
		consoleOutputConfigService.save(ConsoleOutputInfoDto.builder()
				.name(ConsoleOutputInfoDto.DEFAULT_RESOLVER_NAME).build());

		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.repoName("owned-public").isPublic(true).creatorId(CURRENT_USER_ID).build());
		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.repoName("foreign-public").isPublic(true).creatorId(SOMEONE_USER_ID).build());
		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.repoName("owned-private").isPublic(false).creatorId(CURRENT_USER_ID).build());
		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.repoName("foreign-private").isPublic(false).creatorId(SOMEONE_USER_ID).build());

		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.repoName("owned-public-ref").isPublic(true)
				.referencedByUsers(new HashSet<>(Arrays.asList(SOMEONE_USER_ID, 999L)))
				.creatorId(CURRENT_USER_ID).build());
		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.referencedByUsers(new HashSet<>(Arrays.asList(CURRENT_USER_ID, 999L)))
				.repoName("foreign-private-ref").isPublic(false).creatorId(SOMEONE_USER_ID).build());
		databaseService.save(BuildInfoDto.emptyEntityBuilder()
				.referencedByUsers(new HashSet<>(Arrays.asList(888L, 999L)))
				.repoName("foreign-public-ref2").isPublic(true).creatorId(SOMEONE_USER_ID).build());
	}

}
