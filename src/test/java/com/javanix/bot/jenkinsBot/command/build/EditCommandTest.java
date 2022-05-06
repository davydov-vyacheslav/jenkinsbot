package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.CommandTestConfiguration;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EditCommandTest extends AbstractCommandTestCase {

	private  static final String ENTITY_NAME = "repo01";

	@Test
	public void okFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		BuildInfoDto repoInit = BuildInfoDto.builder()
				.repoName(ENTITY_NAME)
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain01")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build();
		Mockito.when(buildInfoService.getOwnedEntityByName(ENTITY_NAME, BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Optional.of(repoInit));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.build.default.mainList", message.getMessageKey());
					return sendResponse;
				});

		executeCommand(from, "/build edit repo01");
		executeCommand(from, "/build edit jenkins.domain");
		executeCommand(from, "Domain02");
		executeCommand(from, "/build edit /done");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));

		Mockito.verify(buildInfoService).save(BuildInfoDto.builder()
				.repoName(ENTITY_NAME)
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
				.repoName(ENTITY_NAME)
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain01")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build();
		Mockito.when(buildInfoService.getOwnedEntityByName(ENTITY_NAME, BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Optional.of(repoInit));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals(getUserInfoString("Domain 01"), message.getMessageKey());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				})
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("error.command.common.save.prefix", message.getMessageKey());
					assertArrayEquals(new Object[] { "error.command.build.validation.invalid.jenkins.domain" }, message.getMessageArgs());
					List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
					assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
					return sendResponse;
				});

		executeCommand(from, "/build edit repo01");
		executeCommand(from, "/build edit jenkins.domain");
		executeCommand(from, "Domain 01");
		executeCommand(from, "/build edit /done");

		Mockito.verify(bot, Mockito.times(3)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(buildInfoService, Mockito.times(0)).save(any());
	}


	@Test
	public void cancelledFlowTest() {
		User from = new User(BuildInfoService.DEFAULT_CREATOR_ID);

		BuildInfoDto repoInit = BuildInfoDto.builder()
				.repoName(ENTITY_NAME)
				.creatorId(BuildInfoService.DEFAULT_CREATOR_ID)
				.isPublic(false)
				.jenkinsInfo(JenkinsInfoDto.builder()
						.domain("Domain01")
						.user("")
						.password("")
						.jobName("Job01")
						.build())
				.build();
		Mockito.when(buildInfoService.getOwnedEntityByName(ENTITY_NAME, BuildInfoService.DEFAULT_CREATOR_ID)).thenReturn(Optional.of(repoInit));
		Mockito.when(bot.sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class)))
				.then(executeEditIntroAndAssert())
				.then(executeEditDomainAndAssert())
				.then(invocation -> {
					TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
					assertEquals("message.command.common.cancel", message.getMessageKey());
					assertArrayEquals(new Object[] {"label.field.common.edit"}, message.getMessageArgs());
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

		executeCommand(from, "/build edit repo01");
		executeCommand(from, "/build edit jenkins.domain");
		executeCommand(from, "Domain02");
		executeCommand(from, "/cancel");

		Mockito.verify(bot, Mockito.times(4)).sendI18nMessage(Mockito.eq(from), any(Chat.class), any(TelegramBotWrapper.MessageInfo.class));
		Mockito.verify(buildInfoService, Mockito.times(0)).save(any());
	}

	private Answer<Object> executeEditIntroAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals("message.command.build.edit.intro", message.getMessageKey());
			assertArrayEquals(new Object[] {ENTITY_NAME, getUserInfoString("Domain01") }, message.getMessageArgs());
			List<InlineKeyboardButton> expectedInlineButtons = getExpectedInlineButtons();
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private Answer<Object> executeEditDomainAndAssert() {
		return invocation -> {
			TelegramBotWrapper.MessageInfo message = invocation.getArgument(2);
			assertEquals(getUserInfoString("Domain02"), message.getMessageKey());
			List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(message);
			assertThat(getExpectedInlineButtons()).containsExactlyInAnyOrderElementsOf(actualInlineButtons);
			return sendResponse;
		};
	}

	private String getUserInfoString(String domain) {
		return String.format("Current repository info: \\n" +
				"- label.field.build.repo.name: repo01\n" +
				"- label.field.build.repo.public: false\n" +
				"- label.field.build.jenkins.domain: %s\n" +
				"- label.field.build.jenkins.user: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.password: \uD83D\uDEAB\n" +
				"- label.field.build.jenkins.job: Job01", domain);
	}

	private List<InlineKeyboardButton> getExpectedInlineButtons() {
		return Arrays.asList(
				new InlineKeyboardButton("Set `label.field.build.repo.public`").callbackData("/build EDIT repo.public"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.domain`").callbackData("/build EDIT jenkins.domain"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.user`").callbackData("/build EDIT jenkins.user"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.password`").callbackData("/build EDIT jenkins.password"),
				new InlineKeyboardButton("Set `label.field.build.jenkins.job`").callbackData("/build EDIT jenkins.job"),
				new InlineKeyboardButton("button.common.complete").callbackData("/build EDIT /done"),
				new InlineKeyboardButton("button.common.cancel").callbackData("/cancel")
		);
	}

	private void executeCommand(User from, String command) {
		factory.getCommand(command).process(chat, from, command);
	}
}