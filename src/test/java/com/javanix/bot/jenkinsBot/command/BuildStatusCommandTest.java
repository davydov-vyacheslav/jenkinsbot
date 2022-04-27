package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.cli.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
public class BuildStatusCommandTest extends AbstractCommandTestCase {

	@Autowired
	private CommandFactory factory;

	@MockBean
	private TelegramBot bot;

	@MockBean
	private Message message;

	@MockBean
	private BuildInfoService databaseService;

	@MockBean
	private CliProcessor cliProcessor;

	@Test
	public void status_noParams() {

		Mockito.when(databaseService.getAvailableRepository("", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(null);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Arrays.asList(
				BuildInfoDto.builder()
						.repoName("repo1")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build(),
				BuildInfoDto.builder()
						.repoName("repo2")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build()));

		Mockito.when(message.text()).thenReturn("/build status");
		Mockito.when(message.chat()).thenReturn(new Chat());
		Mockito.when(message.from()).thenReturn(new User(BuildInfoService.DEFAULT_CREATOR_ID));
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Wrong team. Please choose correct one", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("repo1").switchInlineQueryCurrentChat("/build status repo1"),
					new InlineKeyboardButton("repo2").switchInlineQueryCurrentChat("/build status repo2")
			);
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
	public void status_wrongRepo() {
		Mockito.when(databaseService.getAvailableRepository("xmen", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(null);
		Mockito.when(databaseService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Arrays.asList(
				BuildInfoDto.builder()
						.repoName("repo1")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build(),
				BuildInfoDto.builder()
						.repoName("repo2")
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build()));

		Mockito.when(message.text()).thenReturn("/build status xmen");
		Mockito.when(message.chat()).thenReturn(new Chat());
		Mockito.when(message.from()).thenReturn(new User(BuildInfoService.DEFAULT_CREATOR_ID));
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Wrong team. Please choose correct one", getText(invocation));

			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton("repo1").switchInlineQueryCurrentChat("/build status repo1"),
					new InlineKeyboardButton("repo2").switchInlineQueryCurrentChat("/build status repo2")
			);
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
	public void status_ok() {
		JenkinsInfoDto jenkinsInfo = JenkinsInfoDto.builder()
				.domain("domain")
				.jobName("Insight")
				.build();
		BuildInfoDto team = BuildInfoDto.builder()
				.repoName("xmen")
				.jenkinsInfo(jenkinsInfo)
				.isPublic(false)
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.build();
		Mockito.when(databaseService.getAvailableRepository("xmen", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(team);

		Mockito.when(cliProcessor.getPreviousBuildJenkinsBuildDetails(jenkinsInfo)).thenReturn(
				JenkinsBuildDetails.builder()
						.runTestsCount(1000L)
						.build());
		Mockito.when(cliProcessor.getCurrentBuildJenkinsBuildDetails(jenkinsInfo, 20)).thenReturn(
				JenkinsBuildDetails.builder()
						.runTestsCount(500L)
						.failedTestsCount(2L)
						.failedTestsCapacity(20)
						.topFailedTests(Arrays.asList(
								" [junit] TEST com.liquent.insight.manager.assembly.test.AssemblyExportTest FAILED",
								" [junit] TEST com.liquent.insight.manager.assembly.test2.AnotherFailedTest FAILED"
						))
						.build());

		Mockito.when(message.text()).thenReturn("/build status xmen");
		Mockito.when(message.chat()).thenReturn(new Chat());
		Mockito.when(message.from()).thenReturn(new User(BuildInfoService.DEFAULT_CREATOR_ID));
		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			assertEquals("Build status for `xmen` team:\n" +
							"Run tests: 500 (of approximately 1000)\n" +
							"Top 20 Failed tests (of 2): \n" +
							"- [AssemblyExportTest](http://domain:7331/job/Insight/ws/output/reports/TEST-com.liquent.insight.manager.assembly.test.AssemblyExportTest.xml/*view*/)\n" +
							"- [AnotherFailedTest](http://domain:7331/job/Insight/ws/output/reports/TEST-com.liquent.insight.manager.assembly.test2.AnotherFailedTest.xml/*view*/)",
					getText(invocation));

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
