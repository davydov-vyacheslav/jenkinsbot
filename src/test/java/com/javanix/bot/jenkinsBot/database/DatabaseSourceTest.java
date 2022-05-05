package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// NOTE: DirtiesContext need to refresh mongoTemplate bean after each database down/up
public class DatabaseSourceTest {

	private static final Long CURRENT_USER_ID = 1L;
	private static final Long SOMEONE_USER_ID = 111L;

	private MongodExecutable mongodExecutable;

	@Autowired
	ImmutableMongodConfig mongodConfig;

	@Autowired
	BuildInfoService databaseService;

	@Test
	public void testGetAvailableRepositories() {
		dataSetup();
		List<BuildInfoDto> availableRepositories = databaseService.getAvailableRepositories(CURRENT_USER_ID);
		assertEquals(3, availableRepositories.size());
		assertThat(Arrays.asList("owned-public", "foreign-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(availableRepositories.stream().map(BuildInfoDto::getRepoName).collect(Collectors.toList()));
	}


	@Test
	public void testGetOwnedRepositories() {
		dataSetup();
		List<BuildInfoDto> ownedRepositories = databaseService.getOwnedRepositories(CURRENT_USER_ID);
		assertEquals(2, ownedRepositories.size());
		assertThat(Arrays.asList("owned-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(ownedRepositories.stream().map(BuildInfoDto::getRepoName).collect(Collectors.toList()));
	}

	@Test
	public void testGetAvailableRepository() {
		dataSetup();
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

	private void dataSetup() {
		databaseService.save(BuildInfoDto.builder()
				.repoName("owned-public")
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("domain")
						.build())
				.isPublic(true).creatorId(CURRENT_USER_ID).build());
		databaseService.save(BuildInfoDto.builder()
				.repoName("foreign-public").isPublic(true).creatorId(SOMEONE_USER_ID).build());
		databaseService.save(BuildInfoDto.builder()
				.repoName("owned-private").isPublic(false).creatorId(CURRENT_USER_ID).build());
		databaseService.save(BuildInfoDto.builder()
				.repoName("foreign-private").isPublic(false).creatorId(SOMEONE_USER_ID).build());
	}

	@AfterEach
	void clean() {
		mongodExecutable.stop();
	}

	@BeforeEach
	void start() throws IOException {
		MongodStarter starter = MongodStarter.getDefaultInstance();
		mongodExecutable = starter.prepare(mongodConfig);
		mongodExecutable.start();
	}

}
