package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.javanix.bot.jenkinsBot.command.build.AbstractModifyBuildCommand.ICON_NA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BuildAddCommandTest extends AbstractCommandTestCase {

	@MockBean
	private BuildInfoService databaseService;

	@Test
	public void okFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseService.hasRepository("Repo01")).thenReturn(false);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(executeAddDomainAndAssert())
				.then(executeAddJobNameAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.status.select.title", message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					List<InlineKeyboardButton> expectedInlineButtons = Collections.singletonList(
							new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
					);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, "Repo01");
		executeCommand(from, "/build add jenkins.domain");
		executeCommand(from, "Domain01");
		executeCommand(from, "/build add jenkins.job");
		executeCommand(from, "Job01");
		executeCommand(from, "/build ADD /done");

		Mockito.verify(bot, Mockito.times(5)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(databaseService).addRepository(BuildInfoDto.builder()
						.repoName("Repo01")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.isPublic(false)
						.jenkinsInfo(JenkinsInfoDto.builder()
								.domain("Domain01")
								.user("")
								.password("")
								.jobName("Job01")
								.build())
				.build());
	}

	@Test
	public void failedSaveFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseService.hasRepository("Repo01")).thenReturn(false);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(executeAddDomainAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("error.command.build.save.repo", message.getMessageKey());
					assertArrayEquals(new Object[] { "error.command.build.validation.required.jenkins.job" }, message.getMessageArgs());
					List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, "Repo01");
		executeCommand(from, "/build add jenkins.domain");
		executeCommand(from, "Domain01");
		executeCommand(from, "/build ADD /done");

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(databaseService, Mockito.times(0)).addRepository(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseService.hasRepository("Repo01")).thenReturn(false);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.cancel", message.getMessageKey());
					assertArrayEquals(new Object[] { "label.field.build.common.add" }, message.getMessageArgs());
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
		executeCommand(from, "Repo01");
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(databaseService, Mockito.times(0)).addRepository(any());
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

	private Answer<Object> executeAddDomainAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString("Domain01", ICON_NA), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddJobNameAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString("Domain01", "Job01"), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String domain, String job) {
		return String.format("Current repository info: \\n" +
				"- label.field.build.repo.name: Repo01\n" +
				"- label.field.build.repo.public: false\n" +
				"- label.field.build.jenkins.domain: %s\n" +
				"- label.field.build.jenkins.user: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.password: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.job: %s", domain, job);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set `label.field.build.repo.name`").callbackData("/build ADD repo.name"),
				new InlineKeyboardButton("Set `label.field.build.repo.public`").callbackData("/build ADD repo.public"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.domain`").callbackData("/build ADD jenkins.domain"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.user`").callbackData("/build ADD jenkins.user"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.password`").callbackData("/build ADD jenkins.password"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.job`").callbackData("/build ADD jenkins.job"),
				new InlineKeyboardButton("button.build.common.complete").callbackData("/build ADD /done"),
				new InlineKeyboardButton("button.build.common.cancel").callbackData("/cancel")
		);
	}

	private void executeCommand(User from, String command) {
		factory.getCommand(command).process(chat, from, command);
	}

}
