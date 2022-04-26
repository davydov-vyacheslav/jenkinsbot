package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
public class BuildDeleteCommandTest extends AbstractCommandTestCase {

	@Autowired
	private CommandFactory factory;

	@MockBean
	private TelegramBot bot;

	@MockBean
	private Message message;

	@MockBean
	private BuildInfoService databaseService;

	@Test
	public void delete_noParams() {

		Mockito.when(message.text()).thenReturn("/build delete");
		Mockito.when(message.chat()).thenReturn(new Chat());
		Mockito.when(message.from()).thenReturn(new User(123L));
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Wrong repo. You can delete only owned repository.", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return null;
		});

		TelegramCommand command = factory.getCommand(message.text());
		assertTrue(command instanceof BuildCommand);
		command.process(bot, message);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void delete_wrongRepo() {
		Mockito.when(databaseService.getOwnedRepository("xmen", 123L)).thenReturn(null);

		Mockito.when(message.text()).thenReturn("/build delete xmen");
		Mockito.when(message.chat()).thenReturn(new Chat());
		Mockito.when(message.from()).thenReturn(new User(123L));
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Wrong repo. You can delete only owned repository.", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return null;
		});

		TelegramCommand command = factory.getCommand(message.text());
		assertTrue(command instanceof BuildCommand);
		command.process(bot, message);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void delete_okParams() {
		Mockito.when(message.text()).thenReturn("/build delete xmen");
		Mockito.when(message.chat()).thenReturn(new Chat());
		Mockito.when(message.from()).thenReturn(new User(BuildInfoService.DEFAULT_CREATOR_ID));

		Mockito.when(databaseService.getOwnedRepository("xmen", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(
				BuildInfoDto.builder()
						.repoName("xmen")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build());

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Repository xmen has been removed", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return null;
		});

		TelegramCommand command = factory.getCommand(message.text());
		assertTrue(command instanceof BuildCommand);
		command.process(bot, message);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

}
