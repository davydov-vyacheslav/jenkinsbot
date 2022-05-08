package com.javanix.bot.jenkinsBot.database.settings;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface SettingsRepository extends MongoRepository<SettingsEntity, String> {

	Optional<SettingsEntity> findByKeyIgnoreCase(String key);
}
