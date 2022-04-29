package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.ProgressableCommand;
import com.javanix.bot.jenkinsBot.command.build.model.BuildInfoValidator;
import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation;
import com.javanix.bot.jenkinsBot.command.build.model.StateType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
class AddBuildCommand implements BuildSubCommand, ProgressableCommand {
	private final Map<Long, RepoBuildInformation> userAddInProgressBuilds = new HashMap<>();
	private final BuildInfoService database;
	private final BuildInfoValidator buildInfoValidator;
	private final UserBuildContext userContext;
	private final DefaultBuildCommand defaultBuildCommand;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
		Long currentId = from.id();

		if (!userAddInProgressBuilds.containsKey(currentId)) {
			userAddInProgressBuilds.put(currentId, new RepoBuildInformation(StateType.NA_ADD, BuildInfoDto.emptyEntityBuilder()
					.creatorId(currentId)
					.creatorFullName(from.username())
					.build()));

			showMenu(bot, chat, from, "Okay. Lets create new repository. Press `/cancel` to cancel creation any time");
			return;
		}

		if ("/done".equalsIgnoreCase(buildCommandArguments)) {
			List<String> errors = new ArrayList<>();
			BuildInfoDto repo = userAddInProgressBuilds.get(currentId).getRepo();
			if (buildInfoValidator.validate(repo, BuildType.ADD, errors)) {
				database.addRepository(repo);
				userAddInProgressBuilds.remove(currentId);
				defaultBuildCommand.process(bot, chat, from, "Select build to get build status");
			} else {
				showMenu(bot, chat, from, "Can't save entity. Following issues found:\n-" + String.join("\n-", errors));
			}
		} else {
			userAddInProgressBuilds.get(currentId).setState(StateType.of(buildCommandArguments, StateType.NA_ADD));
		}
	}

	@Override
	public boolean isInProgress(Long userId) {
		return userAddInProgressBuilds.containsKey(userId);
	}

	@Override
	public void cancelProgress(TelegramBot bot, Chat chat, User from) {
		StateType state = userAddInProgressBuilds.get(from.id()).getState();
		bot.execute(new SendMessage(chat.id(), "The command `" + state.getInfo() + "` has been cancelled. Entity discarded"));
		userAddInProgressBuilds.remove(from.id());
		defaultBuildCommand.process(bot, chat, from, "");
	}

	@Override
	public void progress(TelegramBot bot, Chat chat, User from, String value) {
		RepoBuildInformation repoBuildInformation = userAddInProgressBuilds.get(from.id());
		BuildInfoDto repo = repoBuildInformation.getRepo();
		StateType state = repoBuildInformation.getState();
		state.updateField(repo, value);
		showMenu(bot, chat, from, repoBuildInformation.getRepositoryDetails());
		repoBuildInformation.setState(StateType.NA_ADD);
	}

	@Override
	public BuildType getBuildType() {
		return BuildType.ADD;
	}

	private void showMenu(TelegramBot bot, Chat chat, User from, String text) {
		userContext.executeCommandAndSaveMessageId(bot, chat, from,
				new SendMessage(chat.id(), text).replyMarkup(buildCreateRepoMarkup()));
	}

	private InlineKeyboardMarkup buildCreateRepoMarkup() {
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[4][2];

		buttons[0][0] = new InlineKeyboardButton("Set Repo Name").callbackData("/build add repo.name");
		buttons[0][1] = new InlineKeyboardButton("Set Publicity").callbackData("/build add repo.public");

		buttons[1][0] = new InlineKeyboardButton("Set Jenkins Domain️").callbackData("/build add jenkins.domain");
		buttons[1][1] = new InlineKeyboardButton("Set Jenkins User").callbackData("/build add jenkins.user");

		buttons[2][0] = new InlineKeyboardButton("Set Jenkins Password").callbackData("/build add jenkins.password");
		buttons[2][1] = new InlineKeyboardButton("Set Jenkins Job").callbackData("/build add jenkins.job");

		buttons[3][0] = new InlineKeyboardButton("Complete creation ✅").callbackData("/build add /done");
		buttons[3][1] = new InlineKeyboardButton("Cancel creation ❌").callbackData("/cancel");
		return new InlineKeyboardMarkup(buttons);
	}
}
