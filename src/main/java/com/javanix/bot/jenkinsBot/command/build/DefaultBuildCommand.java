package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class DefaultBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final UserBuildContext userContext;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String defaultMessageKey) {
		List<BuildInfoDto> availableRepositories = database.getAvailableRepositories(from.id());

		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(defaultMessageKey.isEmpty() ? "message.command.build.default.mainList" : defaultMessageKey)
				.keyboard(buildMainMenuMarkup(availableRepositories))
				.build());
	}

	@Override
	public CommonEntityActionType getBuildType() {
		return null;
	}

	private InlineKeyboardMarkup buildMainMenuMarkup(List<BuildInfoDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupRepositoriesBy(availableRepositories, 2, inlineKeyboardMarkup, "/build status ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage("button.build.modifyMyItems")).callbackData("/build my_list")
		);

		return inlineKeyboardMarkup;
	}
}
