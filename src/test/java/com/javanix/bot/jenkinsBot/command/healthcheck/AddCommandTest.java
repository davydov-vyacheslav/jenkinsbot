package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractProgressableCommandTest;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand.ICON_NA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class AddCommandTest extends AbstractProgressableCommandTest<HealthCheckInfoDto> {

	@MockBean
	private DefaultHealthCheckCommand defaultHealthCheckCommand;

	@Test
	public void okFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseFactory.getDatabase(any(HealthCheckInfoDto.class))).then(invocation -> healthCheckService);
		Mockito.when(healthCheckService.hasEntity(ENTITY_NAME)).thenReturn(false);
		Mockito.when(healthCheckService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID)).thenReturn(Stream.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddNameAndAssert())
				.then(executeAddUrlAndAssert())
				.then(executeAddPublicAndAssert());

		executeCommand(from, "/healthcheck add");
		executeCommand(from, "/healthcheck add name");
		executeCommand(from, ENTITY_NAME);
		executeCommand(from, "/healthcheck add url");
		executeCommand(from, ENTITY_URL);
		executeCommand(from, "/healthcheck add public");
		executeCommand(from, "true");
		executeCommand(from, "/healthcheck ADD /done");

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultHealthCheckCommand).process(chat, from, "");
		Mockito.verify(healthCheckService).save(getHealthCheckEntity1());
	}

	@Test
	public void failedSaveFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseFactory.getDatabase(any(HealthCheckInfoDto.class))).then(invocation -> healthCheckService);
		Mockito.when(healthCheckService.hasEntity(ENTITY_NAME)).thenReturn(false);
		Mockito.when(healthCheckService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID)).thenReturn(Stream.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddNameAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("error.command.common.save.prefix", message.getMessageKey());
					assertArrayEquals(new Object[] { "error.command.healthcheck.validation.required.url" }, message.getMessageArgs());
					List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/healthcheck add");
		executeCommand(from, "/healthcheck add name");
		executeCommand(from, ENTITY_NAME);
		executeCommand(from, "/healthcheck ADD /done");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(healthCheckService, Mockito.times(0)).save(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(healthCheckService.hasEntity(ENTITY_NAME)).thenReturn(false);
		Mockito.when(healthCheckService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID)).thenReturn(Stream.empty());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddNameAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.common.cancel", message.getMessageKey());
					assertArrayEquals(new Object[] {"label.field.common.add"}, message.getMessageArgs());
					return sendResponse;
				});

		executeCommand(from, "/healthcheck add");
		executeCommand(from, "/healthcheck add name");
		executeCommand(from, ENTITY_NAME);
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(healthCheckService, Mockito.times(0)).save(any());
	}

	private Answer<Object> executeAddIntroAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.healthcheck.add.intro", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddNameAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ICON_NA, "false"), message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddUrlAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ENTITY_URL, "false"), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddPublicAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ENTITY_URL, "true"), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String url, String isPublic) {
		return String.format("Current Endpoint info: \\n" +
				"- label.field.healthcheck.name: " + ENTITY_NAME + "\n" +
				"- label.field.healthcheck.public: %s\n" +
				"- label.field.healthcheck.url: %s", isPublic, url);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set `label.field.healthcheck.name`").callbackData("/healthcheck ADD name"),
				new InlineKeyboardButton("Set `label.field.healthcheck.public`").callbackData("/healthcheck ADD public"),
				new InlineKeyboardButton("Set `label.field.healthcheck.url`").callbackData("/healthcheck ADD url"),
				new InlineKeyboardButton("button.common.complete").callbackData("/healthcheck ADD /done"),
				new InlineKeyboardButton("button.common.cancel").callbackData("/cancel")
		);
	}

}
