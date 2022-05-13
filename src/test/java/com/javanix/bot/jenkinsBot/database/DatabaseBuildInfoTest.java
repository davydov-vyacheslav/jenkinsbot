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
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DatabaseBuildInfoTest extends AbstractDatabaseEntityTest {

	@Autowired
	ConsoleOutputConfigService consoleOutputConfigService;

	@Autowired
	BuildInfoService databaseService;

	@Test
	public void testGetAvailableRepositories() {
		List<BuildInfoDto> availableRepositories = databaseService.getAvailableRepositories(CURRENT_USER_ID);
		assertEquals(3, availableRepositories.size());
		assertThat(Arrays.asList("owned-public", "foreign-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(availableRepositories.stream().map(BuildInfoDto::getRepoName).collect(Collectors.toList()));
	}

	@Test
	public void testGetOwnedEntities() {
		List<BuildInfoDto> ownedEntities = databaseService.getOwnedEntities(CURRENT_USER_ID);
		assertEquals(2, ownedEntities.size());
		assertThat(Arrays.asList("owned-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	@Test
	public void testGetAvailableRepository() {
		BuildInfoDto resultRepositoryEqual = databaseService.getAvailableRepository("owned-public", CURRENT_USER_ID);
		BuildInfoDto resultRepositoryUppercase = databaseService.getAvailableRepository("OWNED-PUBLIC", CURRENT_USER_ID);
		BuildInfoDto resultRepositoryWrongName = databaseService.getAvailableRepository("xyz-public", CURRENT_USER_ID);
		BuildInfoDto resultOwnedPrivateRepository = databaseService.getAvailableRepository("owned-PrivatE", CURRENT_USER_ID);
		BuildInfoDto resultNotOwnedPrivateRepository = databaseService.getAvailableRepository("foreign-private", CURRENT_USER_ID);
		BuildInfoDto resultNotOwnedPublicRepository = databaseService.getAvailableRepository("Foreign-PUBLIC", CURRENT_USER_ID);

		assertAll(() -> {
			assertNotNull(resultRepositoryEqual);
			assertEquals("owned-public", resultRepositoryEqual.getRepoName());
			assertNotNull(resultRepositoryUppercase);
			assertEquals("owned-public", resultRepositoryUppercase.getRepoName());
			assertNotNull(resultNotOwnedPublicRepository);
			assertEquals("foreign-public", resultNotOwnedPublicRepository.getRepoName());
			assertNotNull(resultOwnedPrivateRepository);
			assertEquals("owned-private", resultOwnedPrivateRepository.getRepoName());
			assertNull(resultRepositoryWrongName);
			assertNull(resultNotOwnedPrivateRepository);
		});
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
	}

}
