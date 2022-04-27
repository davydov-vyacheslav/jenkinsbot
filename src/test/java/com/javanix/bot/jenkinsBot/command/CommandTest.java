package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.build.BuildType;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@MockBean(BuildInfoService.class)
public class CommandTest extends AbstractCommandTestCase {

	@Autowired
	private CommandFactory factory;

	@MockBean
	private TelegramBot bot;

	@MockBean
	private Chat chat;

	// TODO: fixme
	@Test
	public void helpCommandTest() {
		String commandText = "/help";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("List of commands: \n" +
					"* /help - This help message\n" +
					"* /build {add,delete,status} - build management and getting actual info", getText(invocation));
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertTrue(command instanceof HelpCommand);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void fooCommandTest() {
		String commandText = "/non-exists asd asd";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Unknown command. Press /help to see list of all commands", getText(invocation));
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertTrue(command instanceof UnknownCommand);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void build_noParams() {
		String commandText = "/build";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Wrong operation. Choose one from list", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton(BuildType.ADD.toString()).callbackData("/build ADD"),
					new InlineKeyboardButton(BuildType.DELETE.toString()).callbackData("/build DELETE"),
					new InlineKeyboardButton(BuildType.STATUS.toString()).callbackData("/build STATUS")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertTrue(command instanceof BuildCommand);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void build_wrongParams() {
		String commandText = "/build xxx";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Wrong operation. Choose one from list", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton(BuildType.ADD.toString()).callbackData("/build ADD"),
					new InlineKeyboardButton(BuildType.DELETE.toString()).callbackData("/build DELETE"),
					new InlineKeyboardButton(BuildType.STATUS.toString()).callbackData("/build STATUS")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertTrue(command instanceof BuildCommand);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void unhandledTextTest() {

		String commandText = "some text";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Okay, what does `some text` mean?", getText(invocation));
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertTrue(command instanceof UnhandledTextCommand);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	// TODO: start command
}
