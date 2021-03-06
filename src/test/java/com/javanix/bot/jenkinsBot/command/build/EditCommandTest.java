package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractProgressableCommandTest;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand.ICON_NA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class EditCommandTest extends AbstractProgressableCommandTest<BuildInfoDto> {

	@MockBean
	private DefaultBuildCommand defaultCommand;

	@Test
	public void okFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.getOwnedEntities(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getBuildInfoEntity1()));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert());

		executeCommand(from, "/build edit " + ENTITY_NAME);
		executeCommand(from, "/build edit jenkins.jobUrl");
		executeCommand(from, ENTITY_URL_2);
		executeCommand(from, "/build edit /done");

		Mockito.verify(bot, Mockito.times(2)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultCommand).process(chat, from, "");
		Mockito.verify(buildInfoService).save(BuildInfoDto.builder()
				.repoName(ENTITY_NAME)
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.jobUrl(ENTITY_URL_2)
						.consoleOutputInfo(ConsoleOutputInfoDto.builder()
								.name("default")
								.unitTestsResultFilepathPrefix("output/reports/TEST-")
								.build())
						.build())
				.referencedByUsers(new HashSet<>())
				.build());
	}

	@Test
	public void failedSaveFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.getOwnedEntities(BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getBuildInfoEntity1()));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals(getUserInfoString(ICON_NA), message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				})
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("error.command.common.save.prefix", message.getMessageKey());
					assertArrayEquals(new Object[] { "error.command.build.validation.required.jenkins.jobUrl" }, message.getMessageArgs());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build edit " + ENTITY_NAME);
		executeCommand(from, "/build edit jenkins.jobUrl");
		executeCommand(from, "");
		executeCommand(from, "/build edit /done");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(buildInfoService, Mockito.times(0)).save(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(EntityService.DEFAULT_CREATOR_ID);

		Mockito.when(buildInfoService.getOwnedEntities(EntityService.DEFAULT_CREATOR_ID)).thenReturn(Stream.of(getBuildInfoEntity1()));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.common.cancel", message.getMessageKey());
					assertArrayEquals(new Object[] {"label.field.common.edit"}, message.getMessageArgs());
					return sendResponse;
				});

		executeCommand(from, "/build edit " + ENTITY_NAME);
		executeCommand(from, "/build edit jenkins.jobUrl");
		executeCommand(from, ENTITY_URL_2);
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(defaultCommand).process(chat, from, "");
		Mockito.verify(buildInfoService, Mockito.times(0)).save(any());
	}

	private Answer<Object> executeEditIntroAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.edit.intro", message.getMessageKey());
			assertArrayEquals(new Object[] {ENTITY_NAME, getUserInfoString(ENTITY_URL) }, message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeEditDomainAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString(ENTITY_URL_2), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String domain) {
		return String.format("Current repository info: \\n" +
				"- label.field.build.repo.name: " + ENTITY_NAME + "\n" +
				"- label.field.build.repo.public: true\n" +
				"- label.field.build.jenkins.jobUrl: %s\n" +
				"- label.field.build.jenkins.user: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.password: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.console.type: default", domain);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set `label.field.build.repo.public`").callbackData("/build EDIT repo.public"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.user`").callbackData("/build EDIT jenkins.user"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.password`").callbackData("/build EDIT jenkins.password"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.jobUrl`").callbackData("/build EDIT jenkins.jobUrl"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.console.type`").callbackData("/build EDIT jenkins.console.type"),
				new InlineKeyboardButton("button.common.complete").callbackData("/build EDIT /done"),
				new InlineKeyboardButton("button.common.cancel").callbackData("/cancel")
		);
	}

}