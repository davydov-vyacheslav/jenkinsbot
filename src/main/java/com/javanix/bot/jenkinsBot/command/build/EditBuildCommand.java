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
import java.util.Optional;

@Component
class EditBuildCommand extends AbstractModifyEntityCommand<BuildInfoDto> implements BuildSubCommand {

	private final MyReposBuildCommand myReposBuildCommand;

	public EditBuildCommand(BuildInfoValidator buildInfoValidator, MyReposBuildCommand myReposBuildCommand, BuildInfoService database,
							UserEntityContext userContext, DefaultBuildCommand defaultBuildCommand, TelegramBotWrapper telegramBotWrapper) {
		super(database, userContext, defaultBuildCommand, buildInfoValidator, telegramBotWrapper);
		this.myReposBuildCommand = myReposBuildCommand;
	}

	@Override
	protected void processOnStart(Chat chat, User from, String command) {
		Optional<BuildInfoDto> repo = database.getOwnedEntityByName(command, from.id());
		if (repo.isPresent()) {
			BuildInfoDto repoDto = repo.get();
			StatedEntity<BuildInfoDto> repoBuildInformation = new StatedEntity<>(repoDto, null);
			usersInProgress.put(from.id(), repoBuildInformation);
			showMenu(chat, from, "message.command.build.edit.intro", new Object[] { repoDto.getRepoName(), getEntityDetails(from, repoDto) } );
		} else {
			myReposBuildCommand.process(chat, from, "error.command.build.edit.repo");
		}
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.EDIT;
	}

	@Override
	protected List<BuildStateType> fieldsToModify() {
		return Arrays.asList(BuildStateType.PUBLIC, BuildStateType.JOB_URL, BuildStateType.USER, BuildStateType.PASSWORD);
	}

	@Override
	protected List<? extends EntityState<BuildInfoDto>> getFieldsToDisplay() {
		return Arrays.asList(BuildStateType.REPO_NAME, BuildStateType.PUBLIC, BuildStateType.JOB_URL, BuildStateType.USER, BuildStateType.PASSWORD);
	}

}
