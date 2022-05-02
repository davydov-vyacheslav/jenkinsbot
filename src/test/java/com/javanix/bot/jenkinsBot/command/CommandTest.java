package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@MockBean(BuildInfoService.class)
public class CommandTest extends AbstractCommandTestCase {

	@Autowired
	private CommandFactory factory;

	@Autowired
	private TelegramBotWrapper bot;

	@MockBean
	private Chat chat;

	@MockBean
	private SendResponse sendResponse;

	@Test
	public void helpCommandTest() {
		String commandText = "/help";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(HelpCommand.class);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(chat, "message.command.help");
	}

	@Test
	public void fooCommandTest() {
		String commandText = "/non-exists asd asd";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(UnknownCommand.class);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(chat, "message.command.unknown");
	}

	@Test
	public void build_noParams() {
		String commandText = "/build";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.getI18nMessage(any())).then(returnsFirstArg());
		Mockito.when(bot.sendI18nMessage(any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(1);
			assertEquals("message.command.build.default.mainList", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
					new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(BuildCommand.class);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void build_wrongParams() {
		String commandText = "/build xxx";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.getI18nMessage(any())).then(returnsFirstArg());
		Mockito.when(bot.sendI18nMessage(any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(1);
			assertEquals("message.command.build.default.mainList", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
					new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(BuildCommand.class);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
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
		command.process(chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

	@Test
	public void unhandledTextTest() {
		String commandText = "some text";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(UnhandledTextCommand.class);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(chat),
				 argThat((TelegramBotWrapper.MessageInfo messageInfo) ->
						messageInfo.getMessageKey().equals("message.command.defaultInProgress.progress")
						&& Arrays.equals(messageInfo.getMessageArgs(), new Object[]{"some text"})
				)
		);
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
		command.process(chat, from, commandText);
		Mockito.verify(bot).execute(any(SetMyCommands.class));
	}

	@Test
	public void cancelCommandTest_noActive() {
		String commandText = "/cancel";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);
		TelegramCommand command = factory.getCommand(commandText);
		assertThat(command).isInstanceOf(CancelCommand.class);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(chat, "message.command.defaultInProgress.cancel");
	}

}
