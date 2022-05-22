package com.javanix.bot.jenkinsBot.command.mysettings;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.NonEntitySubCommand;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DefaultMySettingsCommand implements NonEntitySubCommand {

	private final UserEntityContext userContext;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String defaultMessageKey) {
		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command.my_settings.menu")
				.keyboard(buildMainMenuMarkup(from))
				.build(), EntityType.MY_SETTINGS);
	}

	private InlineKeyboardMarkup buildMainMenuMarkup(User from) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.command.my_settings.language")).callbackData("/my_settings language"),
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.cancel")).callbackData("/my_settings cancel")
		);

		return inlineKeyboardMarkup;
	}

	@Override
	public String getSubCommandName() {
		return "default";
	}


}
