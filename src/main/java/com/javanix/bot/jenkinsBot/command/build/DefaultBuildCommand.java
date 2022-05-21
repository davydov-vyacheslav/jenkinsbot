package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.jenkins.BuildStatus;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsProcessor;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class DefaultBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final UserEntityContext userContext;
	private final TelegramBotWrapper bot;
	private final JenkinsProcessor jenkinsProcessor;

	@Override
	public void process(Chat chat, User from, String defaultMessageKey) {
		List<StatusCheckDto> availableRepositories = database.getOwnedOrReferencedEntities(from.id())
				.map(status -> new StatusCheckDto(status, BuildStatus.NA, 0, 0))
				.collect(Collectors.toList());

		Integer lastMessageId = userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
				.messageKey(defaultMessageKey.isEmpty() ? "message.command.build.default.mainList" : defaultMessageKey)
				.keyboard(buildMainMenuMarkup(from, availableRepositories))
				.build(), EntityType.BUILD_INFO);

		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for (StatusCheckDto repo: availableRepositories) {
			CompletableFuture.runAsync(() -> {

				repo.healthStatus = jenkinsProcessor.getBuildStatus(repo.buildInfoDto.getJenkinsInfo());
				if (repo.healthStatus == BuildStatus.IN_PROGRESS) {
					JenkinsBuildDetails currentBuildJenkinsBuildDetails = jenkinsProcessor.getCurrentBuildJenkinsBuildDetails(repo.buildInfoDto.getJenkinsInfo());
					repo.executedTestsCount = currentBuildJenkinsBuildDetails.getRunTestsCount();
					repo.failedTestsCount = currentBuildJenkinsBuildDetails.getFailedTests().size();
				}

				bot.execute(new EditMessageReplyMarkup(chat.id(), lastMessageId)
						.replyMarkup(buildMainMenuMarkup(from, availableRepositories)));

			}, threadPool);
		}
	}

	@Override
	public EntityActionType getCommandType() {
		return null;
	}

	private InlineKeyboardMarkup buildMainMenuMarkup(User from, List<StatusCheckDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		for (StatusCheckDto availableRepository : availableRepositories) {
			BuildInfoDto buildInfoDto = availableRepository.buildInfoDto;
			String repoName = (buildInfoDto.isPublic() ? ICON_PUBLIC : ICON_PRIVATE);
			if (!buildInfoDto.getCreatorId().equals(from.id())) {
				repoName += ICON_REFERENCE;
			}
			repoName += bot.getI18nMessage(from, availableRepository.healthStatus.getMessageKey()) + " ";
			repoName += buildInfoDto.getName();
			if (availableRepository.healthStatus == BuildStatus.IN_PROGRESS) {
				repoName += String.format(" (%d/%d)", availableRepository.failedTestsCount, availableRepository.executedTestsCount);
			}

			inlineKeyboardMarkup.addRow(
					new InlineKeyboardButton(repoName).callbackData("/build status " + buildInfoDto.getName())
			);
		}
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.refresh_list")).callbackData("/build"),
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.modifyMyItems")).callbackData("/build my_list")
		);

		return inlineKeyboardMarkup;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	static class StatusCheckDto {
		BuildInfoDto buildInfoDto;
		BuildStatus healthStatus;
		Integer failedTestsCount;
		Integer executedTestsCount;
	}
}
