package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class DeleteHealthCheckCommand implements HealthCheckSubCommand {

	private final HealthCheckService database;
	private final DefaultHealthCheckCommand defaultCommand;
	private final MyHealthChecksCommand myHealthChecksCommand;
	private final UserEntityContext userContext;

	@Override
	public void process(Chat chat, User from, String buildCommandArguments) {
		Optional<HealthCheckInfoDto> ownedRepository = database.getOwnedEntityByName(buildCommandArguments, from.id());
		if (ownedRepository.isPresent()) {
			HealthCheckInfoDto repository = ownedRepository.get();
			database.removeEntity(repository.getEndpointName());
			userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
					.messageKey("message.command.endpoint.delete.processed")
					.messageArgs(new Object[] { repository.getEndpointName() })
					.build(), EntityType.HEALTH_CHECK);
			defaultCommand.process(chat, from, "");
		} else {
			myHealthChecksCommand.process(chat, from, "error.command.healthcheck.delete");
		}
	}

	public EntityActionType getCommandType() {
		return EntityActionType.DELETE;
	}

}
