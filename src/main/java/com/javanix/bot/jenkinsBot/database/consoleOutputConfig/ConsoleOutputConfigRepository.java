package com.javanix.bot.jenkinsBot.database.consoleOutputConfig;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface ConsoleOutputConfigRepository extends MongoRepository<ConsoleOutputConfigEntity, String> {

	Optional<ConsoleOutputConfigEntity> findByNameIgnoreCase(String key);
}
