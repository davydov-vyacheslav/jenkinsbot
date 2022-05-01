package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@MockBean
	private SendResponse sendResponse;

	@Test
	public void helpCommandTest() {
		String commandText = "/help";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("JenkinsBot. Основная задача - получение статуса билдов от Дженкиса (состояние билда и кол-во упавших тестов). \n" +
					"Текущая версия заточена на работу с Java/jUnit проектами. \n" +
					"\n" +
					"Смежные команды:\n* /history - Показ содержимого файла с изменениями\n" +
					"\n" +
					"Остальные команды доступны в меню ;)\n" +
					"\n" +
					"Авторы:\n" +
					"* Viacheslav Davydov <davs@javanix.com>\n" +
					"\n" +
					"Со-авторы:\n" +
					"* N/A\n" +
					"\n", getText(invocation));
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(HelpCommand.class);
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
		assertThat(command).isInstanceOf(UnknownCommand.class);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void build_noParams() {
		String commandText = "/build";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Build info main list", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
					new InlineKeyboardButton("Modify My Items ➡️").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(BuildCommand.class);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void build_wrongParams() {
		String commandText = "/build xxx";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Build info main list", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
					new InlineKeyboardButton("Modify My Items ➡️").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(BuildCommand.class);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void historyCommandTest() {
		String commandText = "/history";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertThat(getText(invocation)).contains("What's new / Changelog");
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(HistoryCommand.class);
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
		assertThat(command).isInstanceOf(UnhandledTextCommand.class);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void startCommandTest() {
		String commandText = "/start";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SetMyCommands.class))).then(invocation -> {
			List<String> actualCommandList = Arrays.stream(((BotCommand[])((SetMyCommands)invocation.getArgument(0)).getParameters().get("commands")))
					.map(BotCommand::command)
					.collect(Collectors.toList());
			List<String> expectedValues = Arrays.asList("/help", "/cancel", "/start", "/build");
			assertThat(actualCommandList).containsExactlyInAnyOrderElementsOf(expectedValues);
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(StartCommand.class);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SetMyCommands.class));
	}

	@Test
	public void cancelCommandTest_noActive() {
		String commandText = "/cancel";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("No active command to cancel. I wasn't doing anything anyway. Zzzzz... (c)", getText(invocation));
			return null;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(CancelCommand.class);
		command.process(bot, chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

}
