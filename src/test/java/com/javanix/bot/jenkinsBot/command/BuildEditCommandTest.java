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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BuildEditCommandTest extends AbstractCommandTestCase {

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

		BuildInfoDto repoInit = BuildInfoDto.builder()
				.repoName("repo01")
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain01")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build();
		Mockito.when(databaseService.getOwnedRepository("repo01", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Optional.of(repoInit));
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert())
				.then(invocation -> {
					assertEquals("Select build to get build status", getText(invocation));
					return sendResponse;
				});

		executeCommand(from, "/build edit repo01");
		executeCommand(from, "/build edit jenkins.domain");
		executeCommand(from, "Domain02");
		executeCommand(from, "/build edit /done");

		Mockito.verify(bot, Mockito.times(3)).execute(any(SendMessage.class));

		Mockito.verify(databaseService).updateRepository(BuildInfoDto.builder()
				.repoName("repo01")
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain02")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build());
	}

	@Test
	public void failedSaveFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		BuildInfoDto repoInit = BuildInfoDto.builder()
				.repoName("repo01")
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain01")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build();
		Mockito.when(databaseService.getOwnedRepository("repo01", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Optional.of(repoInit));
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class)))
				.then(executeEditIntroAndAssert())
				.then(invocation -> {
					assertEquals(getUserInfoString("Domain 01"), getText(invocation));
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				})
				.then(invocation -> {
					assertEquals("Can't save entity. Following issues found:\n" +
							"-Jenkins Domain Name is invalid", getText(invocation));
					return sendResponse;
				});

		executeCommand(from, "/build edit repo01");
		executeCommand(from, "/build edit jenkins.domain");
		executeCommand(from, "Domain 01");
		executeCommand(from, "/build edit /done");

		Mockito.verify(bot, Mockito.times(3)).execute(any(SendMessage.class));
		Mockito.verify(databaseService, Mockito.times(0)).updateRepository(any());
		Mockito.verify(databaseService, Mockito.times(0)).addRepository(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		BuildInfoDto repoInit = BuildInfoDto.builder()
				.repoName("repo01")
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain01")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build();
		Mockito.when(databaseService.getOwnedRepository("repo01", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Optional.of(repoInit));
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(bot.execute(any(SendMessage.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert())
				.then(invocation -> {
					assertEquals("The command `Modifying the entity` has been cancelled. Entity discarded", getText(invocation));
					return sendResponse;
				}).then(invocation -> {
					assertEquals("Build info main list", getText(invocation));
					return sendResponse;
				});

		executeCommand(from, "/build edit repo01");
		executeCommand(from, "/build edit jenkins.domain");
		executeCommand(from, "Domain02");
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(4)).execute(any(SendMessage.class));
		Mockito.verify(databaseService, Mockito.times(0)).updateRepository(any());
	}

	private Answer<Object> executeEditIntroAndAssert() {
		return invocation -> {
			assertEquals("Okay. Lets modify `repo01` repository. Press `/cancel` to cancel creation any time \n" +
							getUserInfoString("Domain01"), getText(invocation));
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeEditDomainAndAssert() {
		return invocation -> {
			assertEquals(getUserInfoString("Domain02"), getText(invocation));
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String domain) {
		return String.format("Current repository info:\n" +
				"- repoName: %s\n" +
				"- jenkinsDomain: %s\n" +
				"- jenkinsUser: \uD83D\uDEAB\n" +
				"- jenkinsPassword: \uD83D\uDEAB\n" +
				"- jobName: Job01\n" +
				"- isPublic: false", "repo01", domain);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set Publicity").callbackData("/build EDIT repo.public"),
				new InlineKeyboardButton("Set Jenkins Domain️").callbackData("/build EDIT jenkins.domain"),
				new InlineKeyboardButton("Set Jenkins User").callbackData("/build EDIT jenkins.user"),
				new InlineKeyboardButton("Set Jenkins Password").callbackData("/build EDIT jenkins.password"),
				new InlineKeyboardButton("Set Jenkins Job").callbackData("/build EDIT jenkins.job"),
				new InlineKeyboardButton("Complete action ✅").callbackData("/build EDIT /done"),
				new InlineKeyboardButton("Cancel action ❌").callbackData("/cancel")
		);
	}

	private void executeCommand(User from, String command) {
		factory.getCommand(command).process(bot, chat, from, command);
	}
}