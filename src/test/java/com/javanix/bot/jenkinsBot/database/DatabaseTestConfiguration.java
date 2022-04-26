package com.javanix.bot.jenkinsBot.database;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

@TestConfiguration
@ComponentScan(basePackages = {"com.javanix.bot.jenkinsBot.database"},
		excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MigrationTool.class) }
)
@EnableMongoRepositories(basePackages = "com.javanix.bot.jenkinsBot.database")
public class DatabaseTestConfiguration {

	private static final String CONNECTION_STRING = "mongodb://%s:%d";
	private static final String ip = "localhost";
	private static final int port = 27017;

	@Bean
	public ImmutableMongodConfig getImmutableMongodConfig() throws UnknownHostException {
		return MongodConfig
				.builder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(ip, port, Network.localhostIsIPv6()))
				.build();
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, port)), "test");
	}


}
