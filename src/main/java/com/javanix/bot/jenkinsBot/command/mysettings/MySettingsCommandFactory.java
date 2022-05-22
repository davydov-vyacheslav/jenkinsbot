package com.javanix.bot.jenkinsBot.command.mysettings;

import com.javanix.bot.jenkinsBot.command.common.NonEntitySubCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MySettingsCommandFactory {

	@MySettingsQualifier
	@Autowired
	private List<NonEntitySubCommand> commands;
	private final DefaultMySettingsCommand defaultCommand;

	public NonEntitySubCommand getCommand(String buildType) {
		return commands.stream().filter(command ->
				command.getSubCommandName().equalsIgnoreCase(buildType)).findAny().orElse(defaultCommand);
	}

}
