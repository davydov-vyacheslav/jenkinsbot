package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.command.Processable;
import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

public interface EntitySubCommand<T extends Entity> extends Processable {

	String ICON_PUBLIC = "\uD83C\uDF0E ";
	String ICON_PRIVATE = "\uD83D\uDD12 ";

	EntityActionType getCommandType();

	String getMainCommandName();

	EntityState<T> commandToState(String command);

	default void groupEntitiesBy(List<? extends Entity> entities, int pageSize, InlineKeyboardMarkup inlineKeyboardMarkup, String callbackPrefix) {
		splitListByNElements(pageSize, entities)
				.forEach(entityDtos -> inlineKeyboardMarkup.addRow(
						entityDtos.stream()
								.map(entityDto -> {
									String repoName = (entityDto.isPublic() ? ICON_PUBLIC : ICON_PRIVATE) + entityDto.getName();
									return new InlineKeyboardButton(repoName).callbackData(callbackPrefix + entityDto.getName());
								})
								.toArray(InlineKeyboardButton[]::new)));
	}

}
