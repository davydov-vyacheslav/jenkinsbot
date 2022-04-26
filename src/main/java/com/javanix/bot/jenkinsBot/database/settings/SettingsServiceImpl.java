package com.javanix.bot.jenkinsBot.database.settings;

import com.javanix.bot.jenkinsBot.core.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SettingsServiceImpl implements SettingsService {

	private final SettingsRepository repository;

	@Override
	public String getSetting(String key) {
		return findByKey(key).getValue();
	}

	@Override
	public void saveSettings(String key, String value) {
		SettingsEntity entity = findByKey(key);
		if (entity == null) {
			entity = new SettingsEntity(null, key, value);
		} else {
			entity.setValue(value);
		}
		repository.save(entity);
	}

	private SettingsEntity findByKey(String key) {
		return repository.findByKeyIgnoreCase(key);
	}

}
