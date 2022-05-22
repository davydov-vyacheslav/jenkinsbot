package com.javanix.bot.jenkinsBot.command.mysettings;

import com.javanix.bot.jenkinsBot.command.common.NonEntitySubCommand;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@MySettingsQualifier
@RequiredArgsConstructor
class CancelMySettingsCommand implements NonEntitySubCommand {

	private final UserEntityContext userContext;

	@Override
	public void process(Chat chat, User from, String defaultMessageKey) {
		userContext.executeCommandAndSaveMessageId(chat, from, null, EntityType.MY_SETTINGS);
	}

	@Override
	public String getSubCommandName() {
		return "cancel";
	}
}
