package com.javanix.bot.jenkinsBot.database.settings;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface SettingsRepository extends MongoRepository<SettingsEntity, String> {

	Optional<SettingsEntity> findByKeyIgnoreCase(String key);
}
