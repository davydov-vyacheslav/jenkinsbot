package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation;
import com.javanix.bot.jenkinsBot.command.build.model.StateType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.command.build.validator.BuildInfoAddValidator;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class AddBuildCommand extends AbstractModifyBuildCommand {

	public AddBuildCommand(BuildInfoAddValidator buildInfoValidator, BuildInfoService database, UserBuildContext userContext, DefaultBuildCommand defaultBuildCommand, TelegramBotWrapper telegramBotWrapper) {
		super(database, userContext, defaultBuildCommand, buildInfoValidator, telegramBotWrapper);
	}

	@Override
	protected void processOnStart(Chat chat, User from, String command) {
		userInProgressBuilds.put(from.id(), new RepoBuildInformation(getDefaultInProgressState(), BuildInfoDto.emptyEntityBuilder()
				.creatorId(from.id())
				.creatorFullName(from.username())
				.build()));

		showMenu(chat, from, "message.command.build.add.intro", null);
	}

	@Override
	protected void persist(BuildInfoDto repo) {
		database.addRepository(repo);
	}

	@Override
	protected StateType getDefaultInProgressState() {
		return StateType.NA_ADD;
	}

	@Override
	public CommonEntityActionType getBuildType() {
		return CommonEntityActionType.ADD;
	}

	@Override
	protected List<StateType> fieldsToModify() {
		return Arrays.asList(StateType.REPO_NAME, StateType.PUBLIC, StateType.DOMAIN, StateType.USER, StateType.PASSWORD, StateType.JOB_NAME);
	}
}
