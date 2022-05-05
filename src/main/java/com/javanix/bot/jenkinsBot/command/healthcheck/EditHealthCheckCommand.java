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
import java.util.Optional;

@Component
class EditHealthCheckCommand extends AbstractModifyEntityCommand<HealthCheckInfoDto> implements HealthCheckSubCommand {

	private final MyHealthChecksCommand myReposBuildCommand;

	public EditHealthCheckCommand(HealthCheckValidator validator, MyHealthChecksCommand myHealthChecksCommand, HealthCheckService database,
								  UserEntityContext userContext, DefaultHealthCheckCommand defaultCommand, TelegramBotWrapper telegramBotWrapper) {
		super(database, userContext, defaultCommand, validator, telegramBotWrapper);
		this.myReposBuildCommand = myHealthChecksCommand;
	}

	@Override
	protected void processOnStart(Chat chat, User from, String command) {
		Optional<HealthCheckInfoDto> entity = database.getOwnedEntityByName(command, from.id());
		if (entity.isPresent()) {
			HealthCheckInfoDto entityDto = entity.get();
			StatedEntity<HealthCheckInfoDto> entityBuildInformation = new StatedEntity<>(entityDto, null);
			usersInProgress.put(from.id(), entityBuildInformation);
			showMenu(chat, from, "message.command.endpoint.edit.intro", new Object[] { entityDto.getEndpointName(), getEntityDetails(from, entityDto) } );
		} else {
			myReposBuildCommand.process(chat, from, "error.command.healthcheck.edit.repo");
		}
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.EDIT;
	}

	@Override
	protected List<HealthCheckStateType> fieldsToModify() {
		return Arrays.asList(HealthCheckStateType.PUBLIC, HealthCheckStateType.URL);
	}

	@Override
	protected List<HealthCheckStateType> getFieldsToDisplay() {
		return Arrays.asList(HealthCheckStateType.NAME, HealthCheckStateType.PUBLIC, HealthCheckStateType.URL);
	}

}
