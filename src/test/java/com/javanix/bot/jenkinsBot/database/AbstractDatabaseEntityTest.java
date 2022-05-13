package com.javanix.bot.jenkinsBot.database;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@AutoConfigureDataMongo
public abstract class AbstractDatabaseEntityTest {

	@Autowired
	protected MongoTemplate mongoTemplate;

	protected static final Long CURRENT_USER_ID = 1L;
	protected static final Long SOMEONE_USER_ID = 111L;

	@AfterEach
	void cleanUpDatabase() {
		mongoTemplate.getDb().drop();
	}
}
