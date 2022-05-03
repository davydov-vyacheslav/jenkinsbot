package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.common.CommandFactory;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractEntityCommand implements TelegramCommand {

	@Override
	public void process(Chat chat, User from, String message) {

		// assuming income message is '@botname /build status teamname'
		Matcher m = getCommandPattern().matcher(message);
		CommonEntityActionType buildType = null;
		String buildArguments = "";
		if (m.find()) {
			buildType = getBuildType(m.group(1));
			buildArguments = m.group(2);
		}

		getCommandFactory().getCommand(buildType).process(chat, from, buildArguments);
	}

	protected abstract Pattern getCommandPattern();
	protected abstract CommandFactory getCommandFactory();

	protected CommonEntityActionType getBuildType(String message) {
		CommonEntityActionType type = null;
		try {
			type = CommonEntityActionType.valueOf(message.trim().split(" ")[0].toUpperCase());
		} catch (IllegalArgumentException iae) {
			// no op
		}
		return type;
	}
}
