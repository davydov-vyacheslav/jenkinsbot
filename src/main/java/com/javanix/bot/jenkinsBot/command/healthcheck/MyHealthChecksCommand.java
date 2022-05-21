package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class MyHealthChecksCommand implements HealthCheckSubCommand {

	private final HealthCheckService database;
	private final UserEntityContext userContext;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String defaultMessage) {
		List<HealthCheckInfoDto> availableRepositories = database.getOwnedOrReferencedEntities(from.id()).collect(Collectors.toList());
		InlineKeyboardMarkup inlineKeyboard = buildMyEntitiesListMarkup(from, availableRepositories);
		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(defaultMessage.isEmpty() ? "message.command.healthcheck.list.title" : defaultMessage)
				.keyboard(inlineKeyboard)
				.build(), EntityType.HEALTH_CHECK);
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.MY_LIST;
	}

	private InlineKeyboardMarkup buildMyEntitiesListMarkup(User from, List<HealthCheckInfoDto> availableEntities) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupEntitiesBy(availableEntities, from.id(), 2, inlineKeyboardMarkup, "/healthcheck edit ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.add")).callbackData("/healthcheck add"),
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.add.reference")).callbackData("/healthcheck add_reference"),
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.delete")).switchInlineQueryCurrentChat("/healthcheck delete ")
		);

		return inlineKeyboardMarkup;
	}
}
