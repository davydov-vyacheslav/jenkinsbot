package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DeleteHealthCheckCommand implements HealthCheckSubCommand {

	private final HealthCheckService database;
	private final DefaultHealthCheckCommand defaultCommand;
	private final MyHealthChecksCommand myHealthChecksCommand;
	private final UserEntityContext userContext;

	@Override
	public void process(Chat chat, User from, String endpointName) {
		if (database.removeEntity(from.id(), endpointName)) {
			userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
					.messageKey("message.command.healthcheck.delete.processed")
					.messageArgs(new Object[] { endpointName })
					.build(), EntityType.HEALTH_CHECK);
			defaultCommand.process(chat, from, "");
		} else {
			myHealthChecksCommand.process(chat, from, "error.command.healthcheck.delete");
		}
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.DELETE;
	}

}
