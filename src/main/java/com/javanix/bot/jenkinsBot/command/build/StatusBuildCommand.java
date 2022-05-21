package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.jenkins.BuildStatus;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsProcessor;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
class StatusBuildCommand implements BuildSubCommand {

	private final DefaultBuildCommand defaultBuildCommand;
	private final JenkinsProcessor jenkinsProcessor;
	private final BuildInfoService database;
	private final TelegramBotWrapper bot;

	private static final int FAILED_TESTS_COUNT = 20;

	@Override
	public void process(Chat chat, User from, String buildCommandArguments) {
		BuildInfoDto repository = database.filter(database::getOwnedOrReferencedEntities, from.id(), buildCommandArguments).orElse(null);
		if (repository == null) {
			defaultBuildCommand.process(chat, from, "error.command.build.common.wrongTeam");
			return;
		}

		log.info(from.username() + " is getting status build for team: " + repository.getRepoName());

		showRepositoryInfo(chat, from, repository);
		defaultBuildCommand.process(chat, from, "");
	}

	private void showRepositoryInfo(Chat chat, User from, BuildInfoDto repository) {

		JenkinsBuildDetails currentBuildDetails = jenkinsProcessor.getCurrentBuildJenkinsBuildDetails(repository.getJenkinsInfo());

		// TODO: make build info name as link to Jenkins job
		String resultMessage = bot.getI18nMessage(from, "message.command.build.status.repo_ok", new Object[]{repository.getRepoName(),
				bot.getI18nMessage(from, currentBuildDetails.getBuildStatus().getMessageKey()),
				currentBuildDetails.getRunTestsCount()});

		if (currentBuildDetails.getBuildStatus() == BuildStatus.IN_PROGRESS) {
			JenkinsBuildDetails lastBuildDetails = jenkinsProcessor.getPreviousBuildJenkinsBuildDetails(repository.getJenkinsInfo());
			resultMessage += bot.getI18nMessage(from, "message.command.build.status.repo.suffix.approx", new Object[] {	lastBuildDetails.getRunTestsCount() });
		}

		if (currentBuildDetails.getFailedTests().isEmpty()) {
			resultMessage += bot.getI18nMessage(from, "message.command.build.status.repo.suffix.no_fails");
		} else {
			String failedTestsOutputWithLinks = currentBuildDetails.getFailedTests().stream()
					.limit(FAILED_TESTS_COUNT)
					.map(testName -> String.format("- [%s](%s)\n",
							testName.substring(testName.lastIndexOf(".") + 1),
							jenkinsProcessor.getTestDetailsUrl(repository.getJenkinsInfo(), testName)))
					.collect(Collectors.joining());

			resultMessage += bot.getI18nMessage(from, "message.command.build.status.repo.suffix.fails", new Object[] {
					Integer.min(currentBuildDetails.getFailedTests().size(), FAILED_TESTS_COUNT),
					currentBuildDetails.getFailedTests().size(),
					failedTestsOutputWithLinks
			});
		}

		bot.execute(new SendMessage(chat.id(), resultMessage).parseMode(ParseMode.Markdown));
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.STATUS;
	}
}
