package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.ProgressableCommand;
import com.javanix.bot.jenkinsBot.command.common.validation.EntityValidator;
import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
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

@RequiredArgsConstructor
public abstract class AbstractModifyEntityCommand<DTO extends Entity> implements EntitySubCommand<DTO>, ProgressableCommand {

	private static final String ACTION_DONE = "/done";
	public static final String ICON_NA = "\uD83D\uDEAB";

	protected final EntityService<DTO> database;
	protected final UserEntityContext userContext;
	protected final EntitySubCommand<DTO> defaultCommand;
	protected final EntityValidator<DTO> validator;
	protected final TelegramBotWrapper bot;

	protected final Map<Long, StatedEntity<DTO>> usersInProgress = new HashMap<>();

	protected abstract List<? extends EntityState<DTO>> fieldsToModify();

	protected abstract void processOnStart(Chat chat, User from, String command);

	protected abstract List<? extends EntityState<DTO>> getFieldsToDisplay();

	protected void showMenu(Chat chat, User from, String repoBuildInformationKey, Object[] messageArgs) {
		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(repoBuildInformationKey)
				.messageArgs(messageArgs)
				.keyboard(buildEntityMarkup(from))
				.build(), getEntityType());
	}

	@Override
	public void process(Chat chat, User from, String command) {
		Long currentId = from.id();

		if (!usersInProgress.containsKey(currentId)) {
			processOnStart(chat, from, command);
			return;
		}

		DTO repo = usersInProgress.get(currentId).getEntityDto();
		if (ACTION_DONE.equalsIgnoreCase(command)) {
			List<String> errors = new ArrayList<>();
			if (validator.validate(repo, errors, getCommandType())) {
				database.save(repo);
				usersInProgress.remove(currentId);
				defaultCommand.process(chat, from, "");
			} else {
				String errorsTranslated = errors.stream().map(s -> bot.getI18nMessage(from, s)).collect(Collectors.joining("\n-"));
				showMenu(chat, from, "error.command.common.save.prefix", new Object[] { errorsTranslated });
			}
			return;
		}

		usersInProgress.get(currentId).setState(commandToState(command));

		String fieldWelcomeLabel = String.format("label.welcome.field.%s.%s", getMainCommandName(), command);
		bot.sendI18nMessage(from, chat, fieldWelcomeLabel);
	}

	@Override
	public boolean isInProgress(Long userId) {
		return usersInProgress.containsKey(userId);
	}

	@Override
	public void cancelProgress(Chat chat, User from) {
		EntityState<DTO> state = usersInProgress.get(from.id()).getState();

		String currentAction;

		if (state == null) {
			String labelKey = "label.field.common.edit";
			if (getCommandType() == EntityActionType.ADD) {
				labelKey = "label.field.common.add";
			}
			currentAction = bot.getI18nMessage(from, labelKey);
		} else {
			String fieldLabel = bot.getI18nMessage(from, getFieldLabelKey(state.getFieldKey()));
			currentAction = bot.getI18nMessage(from, "message.command.common.cancel.field", new Object[] { fieldLabel });
		}

		bot.sendI18nMessage(from, chat, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command.common.cancel")
				.messageArgs(new Object[] { currentAction })
				.build());

		usersInProgress.remove(from.id());
		defaultCommand.process(chat, from, "");
	}

	@Override
	public void progress(Chat chat, User from, String value) {
		StatedEntity<DTO> repoBuildInformation = usersInProgress.get(from.id());
		DTO repo = repoBuildInformation.getEntityDto();
		EntityState<DTO> state = repoBuildInformation.getState();
		state.updateField(repo, value);
		showMenu(chat, from, getEntityDetails(from, repo), null);
		repoBuildInformation.setState(null);
	}

	private String getFieldLabelKey(String fieldKey) {
		return String.format("label.field.%s.%s", getMainCommandName(), fieldKey);
	}

	private InlineKeyboardMarkup buildEntityMarkup(User from) {
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

		int pageSize = 2;


		List<? extends EntityState<DTO>> fields = fieldsToModify();
		splitListByNElements(pageSize, fields)
				.forEach(fieldsValues -> inlineKeyboardMarkup.addRow(
						fieldsValues.stream()
								.map(fieldsValue -> getInlineKeyboardButton(from, fieldsValue))
								.toArray(InlineKeyboardButton[]::new)));

		String doneAction = String.format("/%s %s " + ACTION_DONE, getMainCommandName(), getCommandType());
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.complete")).callbackData(doneAction),
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.cancel")).callbackData("/cancel")
		);

		return inlineKeyboardMarkup;
	}

	private InlineKeyboardButton getInlineKeyboardButton(User from, EntityState<DTO> fieldsValue) {
		String fieldName = bot.getI18nMessage(from, getFieldLabelKey(fieldsValue.getFieldKey()));
		String action = String.format("/%s %s %s", getMainCommandName(), getCommandType(), fieldsValue.getFieldKey());
		return new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.setFieldValue", new Object[]{fieldName}))
				.callbackData(action);
	}

	public String getEntityDetails(User from, DTO repo) {
		String fieldsInfo = getFieldsToDisplay().stream()
				.map(stateType -> "- "
						+ bot.getI18nMessage(from, getFieldLabelKey(stateType.getFieldKey()))
						+ ": " + getOrIcon(stateType.getValue(repo)))
				.collect(Collectors.joining("\n"));

		return bot.getI18nMessage(from, "message.command." + getMainCommandName() + ".common.status.prefix", new Object[]{ fieldsInfo });
	}

	private Object getOrIcon(Object value) {
		return (value == null || (value instanceof String && ((String)value).isEmpty())) ? ICON_NA : value;
	}


}
