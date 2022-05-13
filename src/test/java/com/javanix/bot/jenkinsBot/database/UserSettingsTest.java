package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@AutoConfigureDataMongo
public class UserSettingsTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	public static final long USER_ID = 12L;
	public static final String USER_NAME = "someuser";
	public static final LocaleType USER_LOCALE = LocaleType.RU;
	public static final int LAST_MESSAGE_BUILD = 11;
	public static final int LAST_MESSAGE_HEATH = 12;

	@Autowired
	UserService databaseService;

	@Test
	public void testPersistence() {

		databaseService.saveUser(getTestUser());

		UserInfoDto nonExistingUser = databaseService.getUser(123L);
		assertNotNull(nonExistingUser);
		assertEquals(123L, nonExistingUser.getUserId());
		assertEquals("", nonExistingUser.getUserName());
		assertNotNull(nonExistingUser.getLastMessageIdMap());
		assertEquals(0, nonExistingUser.getLastMessageIdMap().size());

		UserInfoDto existingUser = databaseService.getUser(USER_ID);
		assertNotNull(existingUser);
		assertEquals(USER_ID, existingUser.getUserId());
		assertEquals(USER_NAME, existingUser.getUserName());
		assertNotNull(existingUser.getLastMessageIdMap());
		assertEquals(2, existingUser.getLastMessageIdMap().size());
	}

	private UserInfoDto getTestUser() {
		return UserInfoDto.builder()
				.userId(USER_ID)
				.userName(USER_NAME)
				.locale(USER_LOCALE)
				.lastMessageIdMap(new HashMap<EntityType, Integer>() {{
					put(EntityType.BUILD_INFO, LAST_MESSAGE_BUILD);
					put(EntityType.HEALTH_CHECK, LAST_MESSAGE_HEATH);
				}})
				.build();
	}

	@AfterEach
	void cleanUpDatabase() {
		mongoTemplate.getDb().drop();
	}
}
