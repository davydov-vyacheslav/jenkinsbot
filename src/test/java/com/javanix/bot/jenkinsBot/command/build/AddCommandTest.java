package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.CommandTestConfiguration;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand.ICON_NA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AddCommandTest extends AbstractCommandTestCase {

	@Test
	public void okFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseFactory.getDatabase(any(BuildInfoDto.class))).then(invocation -> buildInfoService);
		Mockito.when(buildInfoService.hasEntity(ENTITY_NAME)).thenReturn(false);
		Mockito.when(buildInfoService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(executeAddUserAndAssert())
				.then(executeAddJobNameAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.default.mainList", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
							new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, ENTITY_NAME);
		executeCommand(from, "/build add jenkins.user");
		executeCommand(from, "admin");
		executeCommand(from, "/build add jenkins.jobUrl");
		executeCommand(from, ENTITY_URL);
		executeCommand(from, "/build ADD /done");

		Mockito.verify(bot, Mockito.times(5)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(buildInfoService).save(BuildInfoDto.builder()
						.repoName(ENTITY_NAME)
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.isPublic(false)
						.jenkinsInfo(JenkinsInfoDto.builder()
								.jobUrl(ENTITY_URL)
								.user("admin")
								.password("")
								.consoleOutputInfo(ConsoleOutputInfoDto.emptyEntityBuilder()
										.name("default")
										.build())
								.build())
				.build());
	}

	@Test
	public void failedSaveFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseFactory.getDatabase(any(BuildInfoDto.class))).then(invocation -> buildInfoService);
		Mockito.when(buildInfoService.hasEntity(ENTITY_NAME)).thenReturn(false);
		Mockito.when(buildInfoService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(executeAddUserAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("error.command.common.save.prefix", message.getMessageKey());
					assertArrayEquals(new Object[] { "error.command.build.validation.required.jenkins.jobUrl" }, message.getMessageArgs());
					List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, ENTITY_NAME);
		executeCommand(from, "/build add jenkins.user");
		executeCommand(from, "admin");
		executeCommand(from, "/build ADD /done");

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(buildInfoService, Mockito.times(0)).save(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.hasEntity(ENTITY_NAME)).thenReturn(false);
		Mockito.when(buildInfoService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.common.cancel", message.getMessageKey());
					assertArrayEquals(new Object[] {"label.field.common.add"}, message.getMessageArgs());
					return sendResponse;
				}).then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.default.mainList", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
							new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, ENTITY_NAME);
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(buildInfoService, Mockito.times(0)).save(any());
	}

	private Answer<Object> executeAddIntroAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.add.intro", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddRepoNameAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ICON_NA, ICON_NA), message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddUserAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ICON_NA, "admin"), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddJobNameAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ENTITY_URL, "admin"), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String jobUrl, String user) {
		return String.format("Current repository info: \\n" +
				"- label.field.build.repo.name: " + ENTITY_NAME + "\n" +
				"- label.field.build.repo.public: false\n" +
				"- label.field.build.jenkins.jobUrl: %s\n" +
				"- label.field.build.jenkins.user: %s\n" +
				"- label.field.build.jenkins.password: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.console.type: default", jobUrl, user);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set `label.field.build.repo.name`").callbackData("/build ADD repo.name"),
				new InlineKeyboardButton("Set `label.field.build.repo.public`").callbackData("/build ADD repo.public"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.user`").callbackData("/build ADD jenkins.user"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.password`").callbackData("/build ADD jenkins.password"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.jobUrl`").callbackData("/build ADD jenkins.jobUrl"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.console.type`").callbackData("/build ADD jenkins.console.type"),
				new InlineKeyboardButton("button.common.complete").callbackData("/build ADD /done"),
				new InlineKeyboardButton("button.common.cancel").callbackData("/cancel")
		);
	}

	private void executeCommand(User from, String command) {
		factory.getCommand(command).process(chat, from, command);
	}

}
