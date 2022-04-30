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
class MyReposBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final UserBuildContext userContext;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String defaultMessage) {
		List<BuildInfoDto> availableRepositories = database.getOwnedRepositories(from.id());
		InlineKeyboardMarkup inlineKeyboard = buildMyRepoListMarkup(availableRepositories);
		userContext.executeCommandAndSaveMessageId(bot, chat, from,
				new SendMessage(chat.id(), defaultMessage.isEmpty() ? "My Repositories" : defaultMessage).replyMarkup(inlineKeyboard));
	}


	public BuildType getBuildType() {
		return BuildType.MY_LIST;
	}

	private InlineKeyboardMarkup buildMyRepoListMarkup(List<BuildInfoDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupRepositoriesBy(availableRepositories, 2, inlineKeyboardMarkup, "/build edit ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton("⬅️ Back to action list️").callbackData("/build"),
				new InlineKeyboardButton("Add New ✅").callbackData("/build add"),
				new InlineKeyboardButton("Delete ❌️").switchInlineQueryCurrentChat("/build delete ")
		);

		return inlineKeyboardMarkup;
	}
}
