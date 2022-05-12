package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.cli.jenkins.JenkinsProcessor;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
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
        BuildInfoDto repository = database.getAvailableRepository(buildCommandArguments, from.id());

        if (repository == null) {
            defaultBuildCommand.process(chat, from, "error.command.build.common.wrongTeam");
            return;
        }

        log.info(from.username() + " is getting status build for team: " + repository.getRepoName());

        JenkinsBuildDetails currentBuildDetails = jenkinsProcessor.getCurrentBuildJenkinsBuildDetails(repository.getJenkinsInfo());
        JenkinsBuildDetails lastBuildDetails = jenkinsProcessor.getPreviousBuildJenkinsBuildDetails(repository.getJenkinsInfo());

        String failedTestsOutputWithLinks = "N/A";
        if (!currentBuildDetails.getFailedTests().isEmpty()) {
            failedTestsOutputWithLinks = currentBuildDetails.getFailedTests().stream()
                    .limit(FAILED_TESTS_COUNT)
                    .map(testName -> String.format("- [%s](%s)\n",
                            testName.substring(testName.lastIndexOf(".") + 1),
                            jenkinsProcessor.getTestDetailsUrl(repository.getJenkinsInfo(), testName)))
                    .collect(Collectors.joining());
        }

        bot.sendI18nMessage(from, chat, TelegramBotWrapper.MessageInfo.builder()
                        .messageKey("message.command.build.status.repo")
                        .messageArgs(new Object[] { repository.getRepoName(),
                                currentBuildDetails.getBuildStatus(),
                                bot.getI18nMessage(from, currentBuildDetails.getBuildStatus().getMessageKey()),
                                currentBuildDetails.getRunTestsCount(),
                                lastBuildDetails.getRunTestsCount(),
                                FAILED_TESTS_COUNT,
                                currentBuildDetails.getFailedTests().size(),
                                failedTestsOutputWithLinks})
                        .parseMode(ParseMode.Markdown)
                .build());
        defaultBuildCommand.process(chat, from, "");
    }

    @Override
    public EntityActionType getCommandType() {
        return EntityActionType.STATUS;
    }
}
