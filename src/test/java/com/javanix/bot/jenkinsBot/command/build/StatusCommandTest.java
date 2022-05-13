package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.jenkins.BuildStatus;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsProcessor;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static com.javanix.bot.jenkinsBot.command.common.EntitySubCommand.ICON_PRIVATE;
import static com.javanix.bot.jenkinsBot.command.common.EntitySubCommand.ICON_PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class StatusCommandTest extends AbstractCommandTestCase {

	@MockBean
	private JenkinsProcessor jenkinsProcessor;

	@Test
	public void status_noParams() {
		String commandText = "/build status";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.getAvailableRepository("", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(null);
		Mockito.when(buildInfoService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Arrays.asList(
				BuildInfoDto.builder()
						.repoName(ENTITY_NAME)
						.isPublic(true)
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build(),
				BuildInfoDto.builder()
						.repoName(ENTITY_NAME_2)
						.isPublic(false)
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.build()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.common.wrongTeam", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton(ICON_PUBLIC + ENTITY_NAME).callbackData("/build status " + ENTITY_NAME),
					new InlineKeyboardButton(ICON_PRIVATE + ENTITY_NAME_2).callbackData("/build status " + ENTITY_NAME_2),
					new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void status_wrongRepo() {
		String commandText = "/build status xmen";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.getAvailableRepository("xmen", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(null);
		Mockito.when(buildInfoService.getAvailableRepositories(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Arrays.asList(
				BuildInfoDto.builder()
						.repoName(ENTITY_NAME)
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.isPublic(true)
						.build(),
				BuildInfoDto.builder()
						.repoName(ENTITY_NAME_2)
						.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
						.isPublic(false)
						.build()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.common.wrongTeam", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton(ICON_PUBLIC + ENTITY_NAME).callbackData("/build status " + ENTITY_NAME),
					new InlineKeyboardButton(ICON_PRIVATE + ENTITY_NAME_2).callbackData("/build status " + ENTITY_NAME_2),
					new InlineKeyboardButton("button.build.modifyMyItems").callbackData("/build my_list")
			);
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

	@Test
	public void status_ok() {
		JenkinsInfoDto jenkinsInfo = JenkinsInfoDto.builder()
				.jobUrl("https://domain:7331/job/project")
				.consoleOutputInfo(ConsoleOutputInfoDto.builder()
						.unitTestsResultFilepathPrefix("output/reports/TEST-")
						.build())
				.build();
		BuildInfoDto team = BuildInfoDto.builder()
				.repoName("xmen")
				.jenkinsInfo(jenkinsInfo)
				.isPublic(false)
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.build();
		Mockito.when(buildInfoService.getAvailableRepository("xmen", BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(team);

		Mockito.when(jenkinsProcessor.getPreviousBuildJenkinsBuildDetails(jenkinsInfo)).thenReturn(
				JenkinsBuildDetails.builder()
						.runTestsCount(1000)
						.build());
		Mockito.when(jenkinsProcessor.getCurrentBuildJenkinsBuildDetails(jenkinsInfo)).thenReturn(
				JenkinsBuildDetails.builder()
						.runTestsCount(500)
						.buildStatus(BuildStatus.IN_PROGRESS)
						.failedTests(new LinkedHashSet<String>() {{
								add("com.javanix.jenkinsbot.test.AssemblyExportTest");
								add("com.javanix.jenkinsbot.test2.AnotherFailedTest");
						}})
						.build());
		Mockito.when(jenkinsProcessor.getTestDetailsUrl(any(JenkinsInfoDto.class), any(String.class))).thenCallRealMethod();

		String commandText = "/build status xmen";
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.status.repo", message.getMessageKey());
			assertArrayEquals(new Object[] { "xmen", BuildStatus.IN_PROGRESS, BuildStatus.IN_PROGRESS.getMessageKey(), 500, 1000, 20, 2,
					"- [AssemblyExportTest](https://domain:7331/job/project/ws/output/reports/TEST-com.javanix.jenkinsbot.test.AssemblyExportTest.xml/*view*/)\n" +
							"- [AnotherFailedTest](https://domain:7331/job/project/ws/output/reports/TEST-com.javanix.jenkinsbot.test2.AnotherFailedTest.xml/*view*/)\n" },
					message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		}).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.default.mainList", message.getMessageKey());
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot, times(2)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
	}

}
