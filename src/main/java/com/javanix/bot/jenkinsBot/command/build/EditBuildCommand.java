package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation;
import com.javanix.bot.jenkinsBot.command.build.model.StateType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.command.build.validator.BuildInfoEditValidator;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class EditBuildCommand extends AbstractModifyBuildCommand {

	private final MyReposBuildCommand myReposBuildCommand;

	public EditBuildCommand(BuildInfoEditValidator buildInfoValidator, MyReposBuildCommand myReposBuildCommand, BuildInfoService database, UserBuildContext userContext, DefaultBuildCommand defaultBuildCommand, TelegramBotWrapper telegramBotWrapper) {
		super(database, userContext, defaultBuildCommand, buildInfoValidator, telegramBotWrapper);
		this.myReposBuildCommand = myReposBuildCommand;
	}

	// FIXME: more gracier
	@Override
	protected void processOnStart(Chat chat, User from, String command) {
		database.getOwnedRepository(command, from.id())
				.map(repo -> {
					RepoBuildInformation repoBuildInformation = new RepoBuildInformation(getDefaultInProgressState(), repo);
					userInProgressBuilds.put(from.id(), repoBuildInformation);
					showMenu(chat, from, "message.command.build.edit.intro", new Object[] { repo.getRepoName(), getRepositoryDetails(repo) } );
					return repo;
				})
				.orElseGet(() -> {
					myReposBuildCommand.process(chat, from, "error.command.build.edit.repo");
					return null;
				});
	}

	@Override
	protected void persist(BuildInfoDto repo) {
		database.updateRepository(repo);
	}

	@Override
	protected StateType getDefaultInProgressState() {
		return StateType.NA_EDIT;
	}

	@Override
	public CommonEntityActionType getBuildType() {
		return CommonEntityActionType.EDIT;
	}

	@Override
	protected List<StateType> fieldsToModify() {
		return Arrays.asList(StateType.PUBLIC, StateType.DOMAIN, StateType.USER, StateType.PASSWORD, StateType.JOB_NAME);
	}

}
