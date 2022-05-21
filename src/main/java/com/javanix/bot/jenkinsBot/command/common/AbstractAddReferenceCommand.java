package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractAddReferenceCommand<T extends Entity> implements EntitySubCommand<T> {

	protected final EntityService<T> database;
	protected final UserEntityContext userContext;
	protected final EntitySubCommand<T> defaultCommand;
	protected final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String message) {
		T entity = database.filter(database::getAvailableEntitiesToReference, from.id(), message).orElse(null);
		if (entity != null) {
			entity.getReferences().add(from.id());
			database.save(entity);
			defaultCommand.process(chat, from, "");
			return;
		}

		List<T> availableEntities = database.getAvailableEntitiesToReference(from.id()).collect(Collectors.toList());
		InlineKeyboardMarkup inlineKeyboard = buildRepoListMarkup(from, availableEntities);
		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command." + getMainCommandName() +".add.ref.title")
				.keyboard(inlineKeyboard)
				.build(), getEntityType());
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.ADD_REFERENCE;
	}

	protected abstract InlineKeyboardMarkup buildRepoListMarkup(User from, List<T> availableRepositories);
}
