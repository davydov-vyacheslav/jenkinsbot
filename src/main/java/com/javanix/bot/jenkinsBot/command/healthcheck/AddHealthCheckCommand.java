package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.StatedEntity;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class AddHealthCheckCommand extends AbstractModifyEntityCommand<HealthCheckInfoDto> implements HealthCheckSubCommand {

	public AddHealthCheckCommand(HealthCheckValidator validator, HealthCheckService database, UserEntityContext userContext,
								 DefaultHealthCheckCommand defaultCommand, TelegramBotWrapper telegramBotWrapper) {
		super(database, userContext, defaultCommand, validator, telegramBotWrapper);
	}

	@Override
	protected void processOnStart(Chat chat, User from, String command) {
		usersInProgress.put(from.id(), new StatedEntity<>(HealthCheckInfoDto.emptyEntityBuilder()
				.creatorId(from.id())
				.creatorFullName(from.username())
				.build(), null));

		showMenu(chat, from, "message.command.endpoint.add.intro", null);
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.ADD;
	}

	@Override
	protected List<HealthCheckStateType> fieldsToModify() {
		return Arrays.asList(HealthCheckStateType.NAME, HealthCheckStateType.PUBLIC, HealthCheckStateType.URL);
	}

	@Override
	protected List<HealthCheckStateType> getFieldsToDisplay() {
		return Arrays.asList(HealthCheckStateType.NAME, HealthCheckStateType.PUBLIC, HealthCheckStateType.URL);
	}

}
