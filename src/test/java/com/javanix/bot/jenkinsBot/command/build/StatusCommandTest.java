package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.jenkins.BuildStatus;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsProcessor;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.javanix.bot.jenkinsBot.command.common.EntitySubCommand.ICON_PRIVATE;
import static com.javanix.bot.jenkinsBot.command.common.EntitySubCommand.ICON_PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class StatusCommandTest extends AbstractCommandTestCase {

	@MockBean
	private JenkinsProcessor jenkinsProcessor;

	@Test
	public void status_noParams() {
		String commandText = "/build status";
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.filter(buildInfoService::getOwnedOrReferencedEntities, EntityService.DEFAULT_CREATOR_ID, "")).thenReturn(null);
		Mockito.when(buildInfoService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID)).then(invocation -> Stream.of(
				getBuildInfoEntity1(),
				BuildInfoDto.builder()
						.repoName(ENTITY_NAME_2)
						.isPublic(false)
						.creatorId(EntityService.DEFAULT_CREATOR_ID)
						.build()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.common.wrongTeam", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton(ICON_PUBLIC + ENTITY_NAME).callbackData("/build status " + ENTITY_NAME),
					new InlineKeyboardButton(ICON_PRIVATE + ENTITY_NAME_2).callbackData("/build status " + ENTITY_NAME_2),
					new InlineKeyboardButton("button.common.modifyMyItems").callbackData("/build my_list")
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
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.filter(buildInfoService::getOwnedOrReferencedEntities, EntityService.DEFAULT_CREATOR_ID, "xmen")).thenReturn(null);
		Mockito.when(buildInfoService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID)).then(invocation -> Stream.of(
				getBuildInfoEntity1(),
				BuildInfoDto.builder()
						.repoName(ENTITY_NAME_2)
						.creatorId(EntityService.DEFAULT_CREATOR_ID)
						.isPublic(false)
						.build()));

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("error.command.build.common.wrongTeam", message.getMessageKey());
			List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
					new InlineKeyboardButton(ICON_PUBLIC + ENTITY_NAME).callbackData("/build status " + ENTITY_NAME),
					new InlineKeyboardButton(ICON_PRIVATE + ENTITY_NAME_2).callbackData("/build status " + ENTITY_NAME_2),
					new InlineKeyboardButton("button.common.modifyMyItems").callbackData("/build my_list")
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
		BuildInfoDto buildInfoEntity1 = getBuildInfoEntity1();
		Mockito.when(buildInfoService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID)).then(invocation -> Stream.of(buildInfoEntity1));

		Mockito.when(jenkinsProcessor.getPreviousBuildJenkinsBuildDetails(buildInfoEntity1.getJenkinsInfo())).thenReturn(
				JenkinsBuildDetails.builder()
						.runTestsCount(1000)
						.build());
		Mockito.when(jenkinsProcessor.getCurrentBuildJenkinsBuildDetails(buildInfoEntity1.getJenkinsInfo())).thenReturn(
				JenkinsBuildDetails.builder()
						.runTestsCount(500)
						.buildStatus(BuildStatus.IN_PROGRESS)
						.failedTests(new LinkedHashSet<String>() {{
								add("com.javanix.jenkinsbot.test.AssemblyExportTest");
								add("com.javanix.jenkinsbot.test2.AnotherFailedTest");
						}})
						.build());
		Mockito.when(jenkinsProcessor.getTestDetailsUrl(any(JenkinsInfoDto.class), any(String.class))).thenCallRealMethod();

		String commandText = "/build status " + ENTITY_NAME;
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			SendMessage message = invocation.getArgument(0);
			assertEquals("Build status for `" + ENTITY_NAME + "` repo:\n" +
					"label.command.build.status.type.in_progress Run tests: 500/1 000\n" +
					"Top 2 Failed tests (of 2):\n" +
					"- [AssemblyExportTest](https://domain.com/ws/output/reports/TEST-com.javanix.jenkinsbot.test.AssemblyExportTest.xml/*view*/)\n" +
					"- [AnotherFailedTest](https://domain.com/ws/output/reports/TEST-com.javanix.jenkinsbot.test2.AnotherFailedTest.xml/*view*/)\n", message.getParameters().get("text"));
			return sendResponse;
		});

		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class))).then(invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.default.mainList", message.getMessageKey());
			return sendResponse;
		});

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(bot).execute(any(SendMessage.class));
	}

}
