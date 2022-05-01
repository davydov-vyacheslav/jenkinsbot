package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.javanix.bot.jenkinsBot.command.build.model.RepoBuildInformation.ICON_NA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BuildAddCommandTest extends AbstractCommandTestCase {

	@Autowired
	private CommandFactory factory;

	@MockBean
	private BuildInfoService databaseService;

	@MockBean
	private TelegramBot bot;

	@MockBean
	private Chat chat;

	@MockBean
	private SendResponse sendResponse;

	@Test
	public void okFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseService.hasRepository("Repo01")).thenReturn(false);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(executeAddDomainAndAssert())
				.then(executeAddJobNameAndAssert())
				.then(invocation -> {
					assertEquals("Select build to get build status", getText(invocation));
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

		Mockito.verify(bot, Mockito.times(5)).execute(any(SendMessage.class));
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
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(executeAddDomainAndAssert())
				.then(invocation -> {
					assertEquals("Can't save entity. Following issues found:\n" +
							"-Jenkins Job Name is required", getText(invocation));
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, "Repo01");
		executeCommand(from, "/build add jenkins.domain");
		executeCommand(from, "Domain01");
		executeCommand(from, "/build ADD /done");

		Mockito.verify(bot, Mockito.times(4)).execute(any(SendMessage.class));
		Mockito.verify(databaseService, Mockito.times(0)).addRepository(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(databaseService.hasRepository("Repo01")).thenReturn(false);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Collections.emptyList());
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class)))
				.then(executeAddIntroAndAssert())
				.then(executeAddRepoNameAndAssert())
				.then(invocation -> {
					assertEquals("The command `Adding the entity` has been cancelled. Entity discarded", getText(invocation));
					return sendResponse;
				}).then(invocation -> {
					assertEquals("Build info main list", getText(invocation));
					return sendResponse;
				});

		executeCommand(from, "/build add");
		executeCommand(from, "/build add repo.name");
		executeCommand(from, "Repo01");
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(4)).execute(any(SendMessage.class));
		Mockito.verify(databaseService, Mockito.times(0)).addRepository(any());
	}

	private Answer<Object> executeAddIntroAndAssert() {
		return invocation -> {
			assertEquals("Okay. Lets create new repository. Press `/cancel` to cancel creation any time", getText(invocation));
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddRepoNameAndAssert() {
		return invocation -> {
			assertEquals(getUserInfoString(ICON_NA, ICON_NA), getText(invocation));
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddDomainAndAssert() {
		return invocation -> {
			assertEquals(getUserInfoString("Domain01", ICON_NA), getText(invocation));
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeAddJobNameAndAssert() {
		return invocation -> {
			assertEquals(getUserInfoString("Domain01", "Job01"), getText(invocation));
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String domain, String job) {
		return String.format("Current repository info:\n" +
				"- repoName: %s\n" +
				"- jenkinsDomain: %s\n" +
				"- jenkinsUser: \uD83D\uDEAB\n" +
				"- jenkinsPassword: \uD83D\uDEAB\n" +
				"- jobName: %s\n" +
				"- isPublic: false", "Repo01", domain, job);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set Repo Name").callbackData("/build ADD repo.name"),
				new InlineKeyboardButton("Set Publicity").callbackData("/build ADD repo.public"),
				new InlineKeyboardButton("Set Jenkins Domain️").callbackData("/build ADD jenkins.domain"),
				new InlineKeyboardButton("Set Jenkins User").callbackData("/build ADD jenkins.user"),
				new InlineKeyboardButton("Set Jenkins Password").callbackData("/build ADD jenkins.password"),
				new InlineKeyboardButton("Set Jenkins Job").callbackData("/build ADD jenkins.job"),
				new InlineKeyboardButton("Complete action ✅").callbackData("/build ADD /done"),
				new InlineKeyboardButton("Cancel action ❌").callbackData("/cancel")
		);
	}

	private void executeCommand(User from, String command) {
		factory.getCommand(command).process(bot, chat, from, command);
	}

}
