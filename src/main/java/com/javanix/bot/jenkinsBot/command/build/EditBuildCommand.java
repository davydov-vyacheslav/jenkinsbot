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
class EditBuildCommand implements BuildSubCommand, ProgressableCommand {

	// TODO: merge with Add

	private final Map<Long, RepoBuildInformation> userEditInProgressBuilds = new HashMap<>();
	private final BuildInfoService database;
	private final BuildInfoValidator buildInfoValidator;
	private final UserBuildContext userContext;
	private final DefaultBuildCommand defaultBuildCommand;
	private final MyReposBuildCommand myReposBuildCommand;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String command) {
		Long currentId = from.id();

		if (!userEditInProgressBuilds.containsKey(currentId)) {
			BuildInfoDto repo = database.getOwnedRepository(command, currentId);
			if (repo == null) {
				myReposBuildCommand.process(bot, chat, from, "Wrong repo. You can edit only owned repository.");
				return;
			}
			RepoBuildInformation repoBuildInformation = new RepoBuildInformation(StateType.NA_EDIT, repo);
			userEditInProgressBuilds.put(currentId, repoBuildInformation);
			showMenu(bot, chat, from, String.format("Okay. Lets modify `%s` repository. Press `/cancel` to cancel creation any time \n%s", repo.getRepoName(), repoBuildInformation.getRepositoryDetails()), repo);
			return;
		}

		BuildInfoDto repo = userEditInProgressBuilds.get(currentId).getRepo();
		if ("/done".equalsIgnoreCase(command)) {
			List<String> errors = new ArrayList<>();
			if (buildInfoValidator.validate(repo, BuildType.EDIT, errors)) {
				database.updateRepository(repo);
				userEditInProgressBuilds.remove(currentId);
				defaultBuildCommand.process(bot, chat, from, "Select build to get build status");
			} else {
				showMenu(bot, chat, from, "Can't save entity. Following issues found:\n-" + String.join("\n-", errors), repo);
			}
		} else {
			String action = command.substring(repo.getRepoName().length() + 1);
			userEditInProgressBuilds.get(currentId).setState(StateType.of(action, StateType.NA_EDIT));
		}
	}

	@Override
	public boolean isInProgress(Long userId) {
		return userEditInProgressBuilds.containsKey(userId);
	}

	@Override
	public void cancelProgress(TelegramBot bot, Chat chat, User from) {
		StateType state = userEditInProgressBuilds.get(from.id()).getState();
		bot.execute(new SendMessage(chat.id(), "The command `" + state.getInfo() + "` has been cancelled. Entity discarded"));
		userEditInProgressBuilds.remove(from.id());
		defaultBuildCommand.process(bot, chat, from, "");
	}

	@Override
	public void progress(TelegramBot bot, Chat chat, User from, String value) {
		RepoBuildInformation repoBuildInformation = userEditInProgressBuilds.get(from.id());
		BuildInfoDto repo = repoBuildInformation.getRepo();
		StateType state = repoBuildInformation.getState();
		state.updateField(repo, value);
		showMenu(bot, chat, from, repoBuildInformation.getRepositoryDetails(), repo);
		repoBuildInformation.setState(StateType.NA_EDIT);
	}

	@Override
	public BuildType getBuildType() {
		return BuildType.EDIT;
	}

	private void showMenu(TelegramBot bot, Chat chat, User from, String repoBuildInformation, BuildInfoDto repo) {
		userContext.executeCommandAndSaveMessageId(bot, chat, from,
				new SendMessage(chat.id(), repoBuildInformation).replyMarkup(buildModifyRepoMarkup(repo)));
	}

	private InlineKeyboardMarkup buildModifyRepoMarkup(BuildInfoDto repository) {
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[4][2];

		buttons[0][0] = new InlineKeyboardButton("Edit Publicity").callbackData("/build edit " + repository.getRepoName() + " repo.public");
		buttons[0][1] = new InlineKeyboardButton("Edit Jenkins Domain").callbackData("/build edit " + repository.getRepoName() + " jenkins.domain");

		buttons[1][0] = new InlineKeyboardButton("Edit Jenkins User️").callbackData("/build edit " + repository.getRepoName() + " jenkins.user");
		buttons[1][1] = new InlineKeyboardButton("Edit Jenkins Password").callbackData("/build edit " + repository.getRepoName() + " jenkins.password");

		buttons[2][0] = new InlineKeyboardButton("Edit Jenkins Job").callbackData("/build edit " + repository.getRepoName() + " jenkins.job");
		buttons[2][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");

		buttons[3][0] = new InlineKeyboardButton("Apply changes ✅").callbackData("/build edit /done");
		buttons[3][1] = new InlineKeyboardButton("Cancel editing ❌").callbackData("/cancel");

		return new InlineKeyboardMarkup(buttons);
	}
}
