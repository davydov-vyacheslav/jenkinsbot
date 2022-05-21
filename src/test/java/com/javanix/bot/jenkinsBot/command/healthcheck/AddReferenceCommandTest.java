package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.javanix.bot.jenkinsBot.command.common.EntitySubCommand.ICON_PUBLIC;
import static com.javanix.bot.jenkinsBot.command.common.EntitySubCommand.ICON_REFERENCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class AddReferenceCommandTest extends AbstractCommandTestCase<HealthCheckInfoDto>  {

	@MockBean
	private DefaultHealthCheckCommand defaultHealthCheckCommand;

	@Test
	public void okFlowTest() {
		User from = new User(999L);

		Mockito.when(healthCheckService.getAvailableEntitiesToReference(999L)).then(invocation -> Stream.of(getHealthCheckEntity1()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.healthcheck.add.ref.title", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
							new InlineKeyboardButton(ICON_PUBLIC + ICON_REFERENCE + ENTITY_NAME).callbackData("/healthcheck add_reference " + ENTITY_NAME),
							new InlineKeyboardButton("button.common.backToActionList").callbackData("/healthcheck my_list")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/healthcheck add_reference");
		executeCommand(from, "/healthcheck add_reference " + ENTITY_NAME);

		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultHealthCheckCommand).process(chat, from, "");
		Mockito.verify(healthCheckService).save(HealthCheckInfoDto.builder()
				.endpointName(ENTITY_NAME)
				.endpointUrl(ENTITY_URL)
				.creatorId(EntityService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.referencedByUsers(new HashSet<>(Collections.singletonList(999L)))
				.build());
	}

	@Test
	public void wrongEntityTest() {
		User from = new User(999L);

		Mockito.when(healthCheckService.getAvailableEntitiesToReference(999L)).then(invocation -> Stream.empty());

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.healthcheck.add.ref.title", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
							new InlineKeyboardButton("button.common.backToActionList").callbackData("/healthcheck my_list")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/healthcheck add_reference foo");
		Mockito.verify(bot, Mockito.times(1)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}


}
