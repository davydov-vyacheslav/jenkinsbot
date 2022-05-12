package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;

public interface ConsoleOutputConfigService {

	ConsoleOutputInfoDto findByName(String name);

	void save(ConsoleOutputInfoDto consoleOutputInfoDto);
	// TODO: ability to add/edit by admins

}
