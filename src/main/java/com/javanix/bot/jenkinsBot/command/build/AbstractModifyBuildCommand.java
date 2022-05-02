package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.ProgressableCommand;
import com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation;
import com.javanix.bot.jenkinsBot.command.build.model.StateType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.command.build.validator.Validator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class AbstractModifyBuildCommand implements BuildSubCommand, ProgressableCommand {

	private static final String ACTION_DONE = "/done";
	public static final String ICON_NA = "\uD83D\uDEAB";

	protected final BuildInfoService database;
	protected final UserBuildContext userContext;
	protected final DefaultBuildCommand defaultBuildCommand;
	protected final Validator validator;
	protected final TelegramBotWrapper bot;

	protected final Map<Long, RepoBuildInformation> userInProgressBuilds = new HashMap<>();

	protected abstract StateType getDefaultInProgressState();

	protected abstract List<StateType> fieldsToModify();

	protected abstract void processOnStart(Chat chat, User from, String command);

	protected void showMenu(Chat chat, User from, String repoBuildInformationKey, Object[] messageArgs) {
		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(repoBuildInformationKey)
				.messageArgs(messageArgs)
				.keyboard(buildRepoMarkup())
				.build());
	}

	@Override
	public void process(Chat chat, User from, String command) {
		Long currentId = from.id();

		if (!userInProgressBuilds.containsKey(currentId)) {
			processOnStart(chat, from, command);
			return;
		}

		BuildInfoDto repo = userInProgressBuilds.get(currentId).getRepo();
		if (ACTION_DONE.equalsIgnoreCase(command)) {
			List<String> errors = new ArrayList<>();
			if (validator.validate(repo, errors)) {
				persist(repo);
				userInProgressBuilds.remove(currentId);
				defaultBuildCommand.process(chat, from, "message.command.build.status.select.title");
			} else {
				String errorsTranslated = errors.stream().map(bot::getI18nMessage).collect(Collectors.joining("\n-"));
				showMenu(chat, from, "error.command.build.save.repo", new Object[] { errorsTranslated });
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
	public void cancelProgress(Chat chat, User from) {
		StateType state = userInProgressBuilds.get(from.id()).getState();

		String fieldLabel = bot.getI18nMessage("label.field.build." + state.getFieldKey());
		String currentAction = fieldLabel;
		if (!(state == StateType.NA_ADD || state == StateType.NA_EDIT)) {
			currentAction = bot.getI18nMessage("message.command.build.cancel.field", new Object[] { fieldLabel });
		}

		bot.sendI18nMessage(chat, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command.build.cancel")
				.messageArgs(new Object[] { currentAction })
				.build());

		userInProgressBuilds.remove(from.id());
		defaultBuildCommand.process(chat, from, "");
	}

	@Override
	public void progress(Chat chat, User from, String value) {
		RepoBuildInformation repoBuildInformation = userInProgressBuilds.get(from.id());
		BuildInfoDto repo = repoBuildInformation.getRepo();
		StateType state = repoBuildInformation.getState();
		state.updateField(repo, value);
		showMenu(chat, from, getRepositoryDetails(repo), null);
		repoBuildInformation.setState(getDefaultInProgressState());
	}

	private InlineKeyboardMarkup buildRepoMarkup() {
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

		int pageSize = 2;

		List<StateType> fields = fieldsToModify();
		splitListByNElements(pageSize, fields)
				.forEach(fieldsValues -> inlineKeyboardMarkup.addRow(
						fieldsValues.stream()
								.map(fieldsValue -> new InlineKeyboardButton(bot.getI18nMessage("button.build.setFieldValue", new Object[] { bot.getI18nMessage("label.field.build." + fieldsValue.getFieldKey())}))
										.callbackData("/build " + getBuildType() + " " + fieldsValue.getFieldKey()))
								.toArray(InlineKeyboardButton[]::new)));

		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage("button.build.common.complete")).callbackData("/build " + getBuildType() + " " + ACTION_DONE),
				new InlineKeyboardButton(bot.getI18nMessage("button.build.common.cancel")).callbackData("/cancel")
		);

		return inlineKeyboardMarkup;
	}

	public String getRepositoryDetails(BuildInfoDto repo) {
		String fieldsInfo = Stream.of(StateType.REPO_NAME, StateType.PUBLIC, StateType.DOMAIN, StateType.USER, StateType.PASSWORD, StateType.JOB_NAME)
				.map(stateType -> "- "
						+ bot.getI18nMessage("label.field.build." + stateType.getFieldKey())
						+ ": " + getOrIcon(stateType.getValue(repo)))
				.collect(Collectors.joining("\n"));

		return bot.getI18nMessage("message.command.build.common.repoInfo.prefix", new Object[]{ fieldsInfo });
	}

	private Object getOrIcon(Object value) {
		return (value == null || (value instanceof String && ((String)value).isEmpty())) ? ICON_NA : value;
	}


}
