package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
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

public class AddReferenceCommandTest extends AbstractCommandTestCase  {

	@MockBean
	private DefaultBuildCommand defaultCommand;

	@Test
	public void okFlowTest() {
		User from = new User(999L);

		Mockito.when(buildInfoService.getAvailableEntitiesToReference(999L)).then(invocation -> Stream.of(getBuildInfoEntity1()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.add.ref.title", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
							new InlineKeyboardButton(ICON_PUBLIC + ICON_REFERENCE + ENTITY_NAME).callbackData("/build add_reference " + ENTITY_NAME),
							new InlineKeyboardButton("button.common.backToActionList").callbackData("/build")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add_reference");
		executeCommand(from, "/build add_reference " + ENTITY_NAME);

		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultCommand).process(chat, from, "");
		Mockito.verify(buildInfoService).save(BuildInfoDto.builder()
				.repoName(ENTITY_NAME)
				.creatorId(EntityService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.referencedByUsers(new HashSet<>(Collections.singletonList(999L)))
				.jenkinsInfo(JenkinsInfoDto.builder()
						.jobUrl(ENTITY_URL)
						.consoleOutputInfo(ConsoleOutputInfoDto.builder()
								.name("default")
								.unitTestsResultFilepathPrefix("output/reports/TEST-")
								.build())
						.build())
				.build());
	}

	@Test
	public void wrongEntityTest() {
		User from = new User(999L);

		Mockito.when(buildInfoService.getAvailableEntitiesToReference(999L)).then(invocation -> Stream.empty());

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.add.ref.title", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
							new InlineKeyboardButton("button.common.backToActionList").callbackData("/build")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add_reference foo");
		Mockito.verify(bot, Mockito.times(1)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}


}
