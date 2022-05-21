package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class DeleteCommandTest extends AbstractCommandTestCase {

	@MockBean
	private DefaultBuildCommand defaultCommand;

	@Test
	public void delete_noParams() {
		String commandText = "/build delete";
		User from = new User(123L);

		Mockito.when(buildInfoService.getOwnedEntities(123L)).thenReturn(Stream.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.delete", message.getMessageKey());

			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineKeyboardButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void delete_wrongRepo() {
		String commandText = "/build delete xmen";
		User from = new User(123L);

		Mockito.when(buildInfoService.getOwnedEntities(123L)).thenReturn(Stream.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.delete", message.getMessageKey());

			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineKeyboardButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void delete_okParams() {
		String commandText = "/build delete " + ENTITY_NAME;
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.removeEntity(EntityService.DEFAULT_CREATOR_ID, ENTITY_NAME)).thenReturn(true);
		Mockito.when(buildInfoService.getOwnedEntities(EntityService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getBuildInfoEntity1()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.delete.processed", message.getMessageKey());
			assertArrayEquals(new Object[] { ENTITY_NAME }, message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(defaultCommand).process(chat, from, "");
		Mockito.verify(bot, Mockito.times(1)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@NotNull
	private List<InlineKeyboardButton> getExpectedInlineKeyboardButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("button.common.backToActionList").callbackData("/build"),
				new InlineKeyboardButton("button.common.add.reference").callbackData("/build add_reference"),
				new InlineKeyboardButton("button.common.add").callbackData("/build add"),
				new InlineKeyboardButton("button.common.delete").switchInlineQueryCurrentChat("/build delete ")
		);
	}

}
