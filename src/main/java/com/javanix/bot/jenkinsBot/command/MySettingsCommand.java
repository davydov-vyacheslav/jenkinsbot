package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.mysettings.MySettingsCommandFactory;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
@RequiredArgsConstructor
class MySettingsCommand implements TelegramCommand {

	private final MySettingsCommandFactory mysettingsCommandFactory;

	private static final Pattern MYSETTINGS_COMMAND_PATTERN = Pattern.compile(".*/my_settings.?(language|cancel).?(.*)", CASE_INSENSITIVE);

	@Override
	public void process(Chat chat, User from, String message) {

		// assuming income message is '@botname /build status teamname'
		Matcher m = MYSETTINGS_COMMAND_PATTERN.matcher(message);
		String buildType = "";
		String buildArguments = "";
		if (m.find()) {
			buildType = m.group(1);
			buildArguments = m.group(2);
		}

		mysettingsCommandFactory.getCommand(buildType).process(chat, from, buildArguments);
	}

	@Override
	public String getCommandName() {
		return "/my_settings";
	}

}
