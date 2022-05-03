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
class MyReposBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final UserBuildContext userContext;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String defaultMessage) {
		List<BuildInfoDto> availableRepositories = database.getOwnedRepositories(from.id());
		InlineKeyboardMarkup inlineKeyboard = buildMyRepoListMarkup(availableRepositories);
		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(defaultMessage.isEmpty() ? "message.command.build.myRepos.title" : defaultMessage)
				.keyboard(inlineKeyboard)
				.build());
	}


	public CommonEntityActionType getBuildType() {
		return CommonEntityActionType.MY_LIST;
	}

	private InlineKeyboardMarkup buildMyRepoListMarkup(List<BuildInfoDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupRepositoriesBy(availableRepositories, 2, inlineKeyboardMarkup, "/build edit ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage("button.build.backToActionList")).callbackData("/build"),
				new InlineKeyboardButton(bot.getI18nMessage("button.build.repo.add")).callbackData("/build add"),
				new InlineKeyboardButton(bot.getI18nMessage("button.build.repo.delete")).switchInlineQueryCurrentChat("/build delete ")
		);

		return inlineKeyboardMarkup;
	}
}
