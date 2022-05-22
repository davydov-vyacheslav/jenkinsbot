package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.command.Processable;
import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

public interface EntitySubCommand<T extends Entity> extends Processable {

	String ICON_PUBLIC = "\uD83C\uDF0E ";
	String ICON_PRIVATE = "\uD83D\uDD12 ";
	String ICON_REFERENCE = "\uD83D\uDD17 ";

	EntityActionType getCommandType();

	String getMainCommandName();

	EntityState<T> commandToState(String command);

	EntityType getEntityType();

	default void groupEntitiesBy(List<? extends Entity> entities, Long ownerId, int pageSize, InlineKeyboardMarkup inlineKeyboardMarkup, String callbackPrefix) {
		splitListByNElements(pageSize, entities)
				.forEach(entityDtos -> inlineKeyboardMarkup.addRow(
						entityDtos.stream()
								.map(entityDto -> {
									StringBuilder repoNameBuilder = new StringBuilder(entityDto.isPublic() ? ICON_PUBLIC : ICON_PRIVATE);
									if (!entityDto.getCreatorId().equals(ownerId)) {
										repoNameBuilder.append(ICON_REFERENCE);
									}
									repoNameBuilder.append(entityDto.getName());
									return new InlineKeyboardButton(repoNameBuilder.toString()).callbackData(callbackPrefix + entityDto.getName());
								})
								.toArray(InlineKeyboardButton[]::new)));
	}

}
