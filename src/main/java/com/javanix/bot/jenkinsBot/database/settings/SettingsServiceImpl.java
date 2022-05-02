package com.javanix.bot.jenkinsBot.database.settings;

import com.javanix.bot.jenkinsBot.core.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class SettingsServiceImpl implements SettingsService {

	private final SettingsRepository repository;

	@Override
	public String getSetting(String key) {
		return findByKey(key).orElse(new SettingsEntity()).getValue();
	}

	@Override
	public void saveSettings(String key, String value) {
		SettingsEntity entity = findByKey(key).orElse(new SettingsEntity(null, key, value));
		entity.setValue(value);
		repository.save(entity);
	}

	private Optional<SettingsEntity> findByKey(String key) {
		return repository.findByKeyIgnoreCase(key);
	}

}
