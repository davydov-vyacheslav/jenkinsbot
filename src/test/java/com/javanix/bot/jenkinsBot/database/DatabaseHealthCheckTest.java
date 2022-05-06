package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
// NOTE: DirtiesContext need to refresh mongoTemplate bean after each database down/up
public class DatabaseHealthCheckTest extends AbstractDatabaseEntityTest<HealthCheckInfoDto> {

	private static MongodExecutable mongodExecutable;

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


	private static void dataSetup(HealthCheckService databaseService) {
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

	@BeforeAll
	public static void init(@Autowired ImmutableMongodConfig mongodConfig,
							@Autowired HealthCheckService databaseService) throws IOException {
		MongodStarter starter = MongodStarter.getDefaultInstance();
		mongodExecutable = starter.prepare(mongodConfig);
		mongodExecutable.start();
		dataSetup(databaseService);
	}

	@AfterAll
	public static void tearDown() {
		mongodExecutable.stop();
	}


	@Override
	protected EntityService<HealthCheckInfoDto> getDatabaseService() {
		return databaseService;
	}
}
