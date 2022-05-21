package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand.ICON_NA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class EditCommandTest extends AbstractCommandTestCase {

	@MockBean
	private DefaultHealthCheckCommand defaultHealthCheckCommand;

	@Test
	public void okFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(healthCheckService.getOwnedEntities(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getHealthCheckEntity1()));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert());

		executeCommand(from, "/healthcheck edit " + ENTITY_NAME);
		executeCommand(from, "/healthcheck edit url");
		executeCommand(from, ENTITY_URL_2);
		executeCommand(from, "/healthcheck edit /done");

		Mockito.verify(bot, Mockito.times(2)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultHealthCheckCommand).process(chat, from, "");

		Mockito.verify(healthCheckService).save(HealthCheckInfoDto.builder()
				.endpointName(ENTITY_NAME)
				.endpointUrl(ENTITY_URL_2)
				.referencedByUsers(new HashSet<>())
				.creatorId(HealthCheckService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.build());
	}

	@Test
	public void failedSaveFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(healthCheckService.getOwnedEntities(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getHealthCheckEntity1()));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals(getUserInfoString(ICON_NA), message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				})
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("error.command.common.save.prefix", message.getMessageKey());
					assertArrayEquals(new Object[] { "error.command.healthcheck.validation.required.url" }, message.getMessageArgs());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				})
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.common.cancel", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(Collections.emptyList()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/healthcheck edit " + ENTITY_NAME);
		executeCommand(from, "/healthcheck edit url");
		executeCommand(from, "");
		executeCommand(from, "/healthcheck edit /done");
		executeCommand(from, "/cancel"); // finalize process to remove user from session map

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(healthCheckService, Mockito.times(0)).save(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(healthCheckService.getOwnedEntities(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getHealthCheckEntity1()));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.common.cancel", message.getMessageKey());
					assertArrayEquals(new Object[] {"label.field.common.edit"}, message.getMessageArgs());
					return sendResponse;
				});

		executeCommand(from, "/healthcheck edit " + ENTITY_NAME);
		executeCommand(from, "/healthcheck edit url");
		executeCommand(from, ENTITY_URL_2);
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultHealthCheckCommand).process(chat, from, "");
		Mockito.verify(healthCheckService, Mockito.times(0)).save(any());
	}

	private Answer<Object> executeEditIntroAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.healthcheck.edit.intro", message.getMessageKey());
			assertArrayEquals(new Object[] {ENTITY_NAME, getUserInfoString(ENTITY_URL) }, message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeEditDomainAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ENTITY_URL_2), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String url) {
		return String.format("Current Endpoint info: \\n" +
				"- label.field.healthcheck.name: " + ENTITY_NAME + "\n" +
				"- label.field.healthcheck.public: true\n" +
				"- label.field.healthcheck.url: %s", url);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set `label.field.healthcheck.public`").callbackData("/healthcheck EDIT public"),
				new InlineKeyboardButton("Set `label.field.healthcheck.url`").callbackData("/healthcheck EDIT url"),
				new InlineKeyboardButton("button.common.complete").callbackData("/healthcheck EDIT /done"),
				new InlineKeyboardButton("button.common.cancel").callbackData("/cancel")
		);
	}

}