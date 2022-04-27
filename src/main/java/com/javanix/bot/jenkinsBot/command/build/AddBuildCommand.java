package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.ProgressableCommand;
import com.javanix.bot.jenkinsBot.command.build.add.RepoAddInformation;
import com.javanix.bot.jenkinsBot.command.build.add.StateType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
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
	private final Map<Long, RepoAddInformation> userAddInProgressBuilds = new HashMap<>();
	private final BuildInfoService database;
	private final BuildInfoValidator buildInfoValidator;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
		Long currentId = from.id();

		if (!userAddInProgressBuilds.containsKey(currentId)) {
			userAddInProgressBuilds.put(currentId, new RepoAddInformation(StateType.NA_ADD, BuildInfoDto.emptyEntityBuilder()
					.creatorId(currentId)
					.creatorFullName(from.username())
					.build()));

			bot.execute(new SendMessage(chat.id(), "Okay. Lets create new repository. Press `/cancel` to cancel creation any time")
					.replyMarkup(buildCreateRepoMarkup()));
		}


		List<String> errors = new ArrayList<>();
		if ("/done".equalsIgnoreCase(buildCommandArguments)) {
			BuildInfoDto repo = userAddInProgressBuilds.get(currentId).getRepo();
			if (buildInfoValidator.validate(repo, BuildType.ADD, errors)) {
				database.addRepository(repo);
				userAddInProgressBuilds.remove(currentId);

				List<BuildInfoDto> availableRepositories = database.getAvailableRepositories(from.id());
				InlineKeyboardMarkup inlineKeyboard = buildMainMenuMarkup(availableRepositories);
				bot.execute(new SendMessage(chat.id(), "Select build to get build status").replyMarkup(inlineKeyboard));
			} else {
				bot.execute(new SendMessage(chat.id(), "Can't save entity. Following issues found:\n-" +
						String.join("\n-", errors)).replyMarkup(buildCreateRepoMarkup()));
			}
		} else {
			userAddInProgressBuilds.get(currentId).setState(fieldStatesMap.getOrDefault(buildCommandArguments, StateType.NA_ADD));
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
	}

	@Override
	public void progress(TelegramBot bot, Chat chat, User from, String value) {
		BuildInfoDto repo = userAddInProgressBuilds.get(from.id()).getRepo();
		StateType state = userAddInProgressBuilds.get(from.id()).getState();
		state.updateField(repo, value);
		bot.execute(new SendMessage(chat.id(), getRepositoryDetails(repo)).replyMarkup(buildCreateRepoMarkup()));
		userAddInProgressBuilds.get(from.id()).setState(StateType.NA_ADD);
	}

	@Override
	public BuildType getBuildType() {
		return BuildType.ADD;
	}
}
