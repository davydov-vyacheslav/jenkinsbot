package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.CacheService;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@TestConfiguration
@ComponentScan(basePackages = {"com.javanix.bot.jenkinsBot.database"}) // FIXME: lazyInit=true instead
@EnableMongoRepositories(basePackages = "com.javanix.bot.jenkinsBot.database")
public class DatabaseTestConfiguration {

	@Bean
	public CacheService cacheService() {
		return new CacheService();
	}

	@Bean
	@Primary
	public EmbeddedMongoProperties embeddedMongoProperties() {
		EmbeddedMongoProperties embeddedMongoProperties = new EmbeddedMongoProperties();
		embeddedMongoProperties.setVersion(Version.V5_0_6.asInDownloadPath());
		return embeddedMongoProperties;
	}

}
