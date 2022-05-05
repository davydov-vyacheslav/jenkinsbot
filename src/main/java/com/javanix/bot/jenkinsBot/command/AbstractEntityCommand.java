package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.EntityCommandFactory;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractEntityCommand implements TelegramCommand {

	@Override
	public void process(Chat chat, User from, String message) {

		// assuming income message is '@botname /build status teamname'
		Matcher m = getCommandPattern().matcher(message);
		EntityActionType buildType = null;
		String buildArguments = "";
		if (m.find()) {
			buildType = getBuildType(m.group(1));
			buildArguments = m.group(2);
		}

		getCommandFactory().getCommand(buildType).process(chat, from, buildArguments);
	}

	protected abstract Pattern getCommandPattern();
	protected abstract EntityCommandFactory getCommandFactory();

	protected EntityActionType getBuildType(String message) {
		EntityActionType type = null;
		try {
			type = EntityActionType.valueOf(message.trim().split(" ")[0].toUpperCase());
		} catch (IllegalArgumentException iae) {
			// no op
		}
		return type;
	}
}
