package com.javanix.bot.jenkinsBot.core.service;

public interface SettingsService {

	String KEY_DB_VERSION = "dbVersion";

	String getSetting(String key);

	void saveSettings(String key, String value);

}
