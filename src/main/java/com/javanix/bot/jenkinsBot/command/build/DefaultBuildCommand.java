package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
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
class DefaultBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final UserEntityContext userContext;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String defaultMessageKey) {
		List<BuildInfoDto> availableRepositories = database.getOwnedOrReferencedEntities(from.id()).collect(Collectors.toList());

		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(defaultMessageKey.isEmpty() ? "message.command.build.default.mainList" : defaultMessageKey)
				.keyboard(buildMainMenuMarkup(from, availableRepositories))
				.build(), EntityType.BUILD_INFO);
	}

	@Override
	public EntityActionType getCommandType() {
		return null;
	}

	private InlineKeyboardMarkup buildMainMenuMarkup(User from, List<BuildInfoDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupEntitiesBy(availableRepositories, from.id(), 2, inlineKeyboardMarkup, "/build status ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.modifyMyItems")).callbackData("/build my_list")
		);

		return inlineKeyboardMarkup;
	}
}
