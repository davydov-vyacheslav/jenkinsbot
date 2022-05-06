package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.EntityState;
import com.javanix.bot.jenkinsBot.command.common.StatedEntity;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class AddBuildCommand extends AbstractModifyEntityCommand<BuildInfoDto> implements BuildSubCommand {

	public AddBuildCommand(BuildInfoValidator buildInfoValidator, BuildInfoService database, UserEntityContext userContext,
						   DefaultBuildCommand defaultBuildCommand, TelegramBotWrapper telegramBotWrapper) {
		super(database, userContext, defaultBuildCommand, buildInfoValidator, telegramBotWrapper);
	}

	@Override
	protected void processOnStart(Chat chat, User from, String command) {
		usersInProgress.put(from.id(), new StatedEntity<>(BuildInfoDto.emptyEntityBuilder()
				.creatorId(from.id())
				.creatorFullName(from.username())
				.build(), null));

		showMenu(chat, from, "message.command.build.add.intro", null);
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.ADD;
	}

	@Override
	protected List<BuildStateType> fieldsToModify() {
		return Arrays.asList(BuildStateType.REPO_NAME, BuildStateType.PUBLIC, BuildStateType.JOB_URL, BuildStateType.USER, BuildStateType.PASSWORD);
	}

	@Override
	protected List<? extends EntityState<BuildInfoDto>> getFieldsToDisplay() {
		return Arrays.asList(BuildStateType.REPO_NAME, BuildStateType.PUBLIC, BuildStateType.JOB_URL, BuildStateType.USER, BuildStateType.PASSWORD);
	}

}
