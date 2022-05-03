package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DefaultHealthCheckCommand implements HealthCheckSubCommand {

	private final StatusHealthCheckCommand statusHealthCheckCommand;

	@Override
	public void process(Chat chat, User from, String defaultMessageKey) {
		statusHealthCheckCommand.process(chat, from, defaultMessageKey);
	}

	@Override
	public CommonEntityActionType getBuildType() {
		return null;
	}

}
