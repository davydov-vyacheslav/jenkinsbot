package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.CommandTestConfiguration;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
public class BuildDeleteCommandTest extends AbstractCommandTestCase {

	@MockBean
	private BuildInfoService databaseService;

	@Test
	public void delete_noParams() {
		String commandText = "/build delete";
		User from = new User(123L);

		Mockito.when(databaseService.getOwnedEntityByName("", 123L)).thenReturn(Optional.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.delete", message.getMessageKey());

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("button.build.backToActionList").callbackData("/build"),
					new InlineKeyboardButton("button.common.add").callbackData("/build add"),
					new InlineKeyboardButton("button.common.delete").switchInlineQueryCurrentChat("/build delete ")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

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

		Mockito.when(databaseService.getOwnedEntityByName("xmen", 123L)).thenReturn(Optional.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.delete", message.getMessageKey());

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("button.build.backToActionList").callbackData("/build"),
					new InlineKeyboardButton("button.common.add").callbackData("/build add"),
					new InlineKeyboardButton("button.common.delete").switchInlineQueryCurrentChat("/build delete ")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void delete_okParams() {
		String commandText = "/build delete xmen";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseService.getOwnedEntityByName("xmen", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(
				Optional.of(BuildInfoDto.builder()
						.repoName("xmen")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.delete.processed", message.getMessageKey());
			assertArrayEquals(new Object[] { "xmen" }, message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		}).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.default.mainList", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
					new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot, Mockito.times(2)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

}
