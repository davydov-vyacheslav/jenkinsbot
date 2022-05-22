package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class MySettingsCommandTest extends AbstractCommandTestCase {

	@Test
	public void mySettingsCommandTest() {
		String commandText = "/my_settings";
		User from = new User(EntityService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(MySettingsCommand.class);

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.my_settings.menu", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("button.command.my_settings.language").callbackData("/my_settings language"),
					new InlineKeyboardButton("button.common.cancel").callbackData("/my_settings cancel")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void mySettingsLanguageCommandTest() {
		String commandText = "/my_settings language";
		User from = new User(EntityService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.my_settings.languages.list", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("button.command.my_settings.language.type.en").callbackData("/my_settings language EN"),
					new InlineKeyboardButton("button.command.my_settings.language.type.ru").callbackData("/my_settings language RU"),
					new InlineKeyboardButton("button.command.my_settings.language.type.uk").callbackData("/my_settings language UK"),
					new InlineKeyboardButton("button.common.cancel").callbackData("/my_settings cancel")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void mySettingsLanguageSelectCommandTest() {
		String commandText = "/my_settings language uk";
		User from = new User(EntityService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(userService).updateUserLocale(from.id(), LocaleType.UK);
		Mockito.verify(userService).saveUser(UserInfoDto.emptyEntityBuilder()
				.userName(null)
				.lastMessageIdMap(new HashMap<EntityType, Integer>() {{
					put(EntityType.MY_SETTINGS, null);
				}})
				.build());
	}

	@Test
	public void cancelMySettingsCommandTest() {
		String commandText = "/my_settings cancel";
		User from = new User(EntityService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(userService).saveUser(UserInfoDto.emptyEntityBuilder()
				.userName(null)
				.lastMessageIdMap(new HashMap<EntityType, Integer>() {{
					put(EntityType.MY_SETTINGS, null);
				}})
				.build());
	}
}
