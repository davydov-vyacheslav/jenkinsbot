package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.service.SettingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@AutoConfigureDataMongo
public class SystemSettingsTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	SettingsService databaseService;

	@Test
	public void testSave() {
		databaseService.saveSettings(SettingsService.KEY_DB_VERSION, "0.0.1");
		databaseService.saveSettings(SettingsService.KEY_DB_VERSION, "0.0.2");
		databaseService.saveSettings("other", "value");

		String fooSetting = databaseService.getSetting("foo");
		assertNull(fooSetting);

		assertEquals("0.0.2", databaseService.getSetting(SettingsService.KEY_DB_VERSION));
		assertEquals("value", databaseService.getSetting("other"));
	}


	@AfterEach
	void cleanUpDatabase() {
		mongoTemplate.getDb().drop();
	}
}
