package com.javanix.bot.jenkinsBot.database.settings;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SettingsRepository extends MongoRepository<SettingsEntity, String> {

	SettingsEntity findByKeyIgnoreCase(String key);
}
