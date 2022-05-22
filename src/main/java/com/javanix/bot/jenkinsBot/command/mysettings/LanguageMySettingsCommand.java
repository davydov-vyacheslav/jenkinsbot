package com.javanix.bot.jenkinsBot.command.mysettings;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.NonEntitySubCommand;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
@MySettingsQualifier
@RequiredArgsConstructor
class LanguageMySettingsCommand implements NonEntitySubCommand {

	private final UserService userService;
	private final UserEntityContext userContext;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String language) {

		if (!language.trim().isEmpty()) {
			userService.updateUserLocale(from.id(), LocaleType.of(language));
			userContext.executeCommandAndSaveMessageId(chat, from, null, EntityType.MY_SETTINGS);
			return;
		}

		userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command.my_settings.languages.list")
				.keyboard(buildMyRepoListMarkup(from))
				.build(), EntityType.MY_SETTINGS);
	}

	private InlineKeyboardMarkup buildMyRepoListMarkup(User from) {
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		inlineKeyboardMarkup.addRow(
				Arrays.stream(LocaleType.values())
						.map(localeType -> new InlineKeyboardButton(
								bot.getI18nMessage(from, "button.command.my_settings.language.type." + localeType.name().toLowerCase(Locale.ENGLISH)))
								.callbackData("/my_settings language " + localeType))
						.toArray(InlineKeyboardButton[]::new)
		);
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.cancel")).callbackData("/my_settings cancel")
		);

		return inlineKeyboardMarkup;
	}

	@Override
	public String getSubCommandName() {
		return "language";
	}
}
