package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation;
import com.javanix.bot.jenkinsBot.command.build.model.StateType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.command.build.validator.BuildInfoEditValidator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class EditBuildCommand extends AbstractModifyBuildCommand {

	private final MyReposBuildCommand myReposBuildCommand;

	public EditBuildCommand(BuildInfoEditValidator buildInfoValidator, MyReposBuildCommand myReposBuildCommand, BuildInfoService database, UserBuildContext userContext, DefaultBuildCommand defaultBuildCommand) {
		super(database, userContext, defaultBuildCommand, buildInfoValidator);
		this.myReposBuildCommand = myReposBuildCommand;
	}

	// FIXME: more gracier
	@Override
	protected void processOnStart(TelegramBot bot, Chat chat, User from, String command) {
		database.getOwnedRepository(command, from.id())
				.map(repo -> {
					RepoBuildInformation repoBuildInformation = new RepoBuildInformation(getDefaultInProgressState(), repo);
					userInProgressBuilds.put(from.id(), repoBuildInformation);
					showMenu(bot, chat, from, String.format("Okay. Lets modify `%s` repository. Press `/cancel` to cancel creation any time \n%s", repo.getRepoName(), repoBuildInformation.getRepositoryDetails()));
					return null;
				})
				.orElseGet(() -> {
					myReposBuildCommand.process(bot, chat, from, "Wrong repo. You can edit only owned repository.");
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
	public BuildType getBuildType() {
		return BuildType.EDIT;
	}

	@Override
	protected List<StateType> fieldsToModify() {
		return Arrays.asList(StateType.PUBLIC, StateType.DOMAIN, StateType.USER, StateType.PASSWORD, StateType.JOB_NAME);
	}

}
