package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.ProgressableCommand;
import com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation;
import com.javanix.bot.jenkinsBot.command.build.model.StateType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.command.build.validator.Validator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractModifyBuildCommand implements BuildSubCommand, ProgressableCommand {

	private final static String ACTION_DONE = "/done";

	protected final BuildInfoService database;
	protected final UserBuildContext userContext;
	protected final DefaultBuildCommand defaultBuildCommand;
	protected final Validator validator;

	protected final Map<Long, RepoBuildInformation> userInProgressBuilds = new HashMap<>();

	protected abstract StateType getDefaultInProgressState();

	protected abstract List<StateType> fieldsToModify();

	protected abstract void processOnStart(TelegramBot bot, Chat chat, User from, String command);

	protected void showMenu(TelegramBot bot, Chat chat, User from, String repoBuildInformation) {
		userContext.executeCommandAndSaveMessageId(bot, chat, from,
				new SendMessage(chat.id(), repoBuildInformation).replyMarkup(buildRepoMarkup()));
	}

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String command) {
		Long currentId = from.id();

		if (!userInProgressBuilds.containsKey(currentId)) {
			processOnStart(bot, chat, from, command);
			return;
		}

		BuildInfoDto repo = userInProgressBuilds.get(currentId).getRepo();
		if (ACTION_DONE.equalsIgnoreCase(command)) {
			List<String> errors = new ArrayList<>();
			if (validator.validate(repo, errors)) {
				persist(repo);
				userInProgressBuilds.remove(currentId);
				defaultBuildCommand.process(bot, chat, from, "Select build to get build status");
			} else {
				showMenu(bot, chat, from, "Can't save entity. Following issues found:\n-" + String.join("\n-", errors));
			}
			return;
		}

		userInProgressBuilds.get(currentId).setState(StateType.of(command, getDefaultInProgressState()));
	}

	protected abstract void persist(BuildInfoDto repo);

	@Override
	public boolean isInProgress(Long userId) {
		return userInProgressBuilds.containsKey(userId);
	}

	@Override
	public void cancelProgress(TelegramBot bot, Chat chat, User from) {
		StateType state = userInProgressBuilds.get(from.id()).getState();
		bot.execute(new SendMessage(chat.id(), "The command `" + state.getInfo() + "` has been cancelled. Entity discarded"));
		userInProgressBuilds.remove(from.id());
		defaultBuildCommand.process(bot, chat, from, "");
	}

	@Override
	public void progress(TelegramBot bot, Chat chat, User from, String value) {
		RepoBuildInformation repoBuildInformation = userInProgressBuilds.get(from.id());
		BuildInfoDto repo = repoBuildInformation.getRepo();
		StateType state = repoBuildInformation.getState();
		state.updateField(repo, value);
		showMenu(bot, chat, from, repoBuildInformation.getRepositoryDetails());
		repoBuildInformation.setState(getDefaultInProgressState());
	}

	private InlineKeyboardMarkup buildRepoMarkup() {
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

		int pageSize = 2;

		List<StateType> fields = fieldsToModify();
		splitListByNElements(pageSize, fields)
				.forEach(fieldsValues -> inlineKeyboardMarkup.addRow(
						fieldsValues.stream()
								.map(fieldsValue -> new InlineKeyboardButton("Set " + fieldsValue.getFieldName())
										.callbackData("/build " + getBuildType() + " " + fieldsValue.getFieldKey()))
								.toArray(InlineKeyboardButton[]::new)));

		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton("Complete action ✅").callbackData("/build " + getBuildType() + " " + ACTION_DONE),
				new InlineKeyboardButton("Cancel action ❌").callbackData("/cancel")
		);

		return inlineKeyboardMarkup;
	}

}
