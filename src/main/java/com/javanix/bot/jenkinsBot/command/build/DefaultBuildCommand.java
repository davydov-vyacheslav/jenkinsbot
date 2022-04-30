package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class DefaultBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final UserBuildContext userContext;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String defaultMessage) {
		List<BuildInfoDto> availableRepositories = database.getAvailableRepositories(from.id());

		userContext.executeCommandAndSaveMessageId(bot, chat, from,
				new SendMessage(chat.id(), defaultMessage.isEmpty() ? "Build info main list" : defaultMessage).replyMarkup(buildMainMenuMarkup(availableRepositories)));
	}

	@Override
	public BuildType getBuildType() {
		return null;
	}

	private InlineKeyboardMarkup buildMainMenuMarkup(List<BuildInfoDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupRepositoriesBy(availableRepositories, 2, inlineKeyboardMarkup, "/build status ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton("Modify My Items ➡️").callbackData("/build my_list")
		);

		return inlineKeyboardMarkup;
	}
}
