package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.CommandTestConfiguration;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
public class DeleteCommandTest extends AbstractCommandTestCase {

	private static final String ENTITY_NAME = "Endpoint01";

	@Test
	public void delete_noParams() {
		String commandText = "/healthcheck delete";
		User from = new User(123L);

		Mockito.when(healthCheckService.getOwnedEntityByName("", 123L)).thenReturn(Optional.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.healthcheck.delete", message.getMessageKey());

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("button.common.add").callbackData("/healthcheck add"),
					new InlineKeyboardButton("button.common.delete").switchInlineQueryCurrentChat("/healthcheck delete ")
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
		String commandText = "/healthcheck delete " + ENTITY_NAME;
		User from = new User(123L);

		Mockito.when(healthCheckService.getOwnedEntityByName(ENTITY_NAME, 123L)).thenReturn(Optional.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.healthcheck.delete", message.getMessageKey());

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("button.common.add").callbackData("/healthcheck add"),
					new InlineKeyboardButton("button.common.delete").switchInlineQueryCurrentChat("/healthcheck delete ")
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
		String commandText = "/healthcheck delete " + ENTITY_NAME;
		User from = new User(HealthCheckService.DEFAULT_CREATOR_ID);

		Mockito.when(healthCheckService.getOwnedEntityByName(ENTITY_NAME, HealthCheckService.DEFAULT_CREATOR_ID)).thenReturn(
				Optional.of(HealthCheckInfoDto.builder()
						.endpointName(ENTITY_NAME)
						.endpointUrl("https://someul.com/")
						.creatorId(HealthCheckService.DEFAULT_CREATOR_ID)
						.isPublic(true)
						.build()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.healthcheck.delete.processed", message.getMessageKey());
			assertArrayEquals(new Object[] { ENTITY_NAME }, message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot, Mockito.times(1)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

}
