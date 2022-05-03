package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.cli.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Log4j2
class StatusBuildCommand implements BuildSubCommand {

    private final DefaultBuildCommand defaultBuildCommand;
    private final CliProcessor cliProcessor;
    private final BuildInfoService database;
    private final TelegramBotWrapper bot;
    private static final int failedTestsCount = 20;
    private static final Pattern failedTestsPattern = Pattern.compile(".*\\[junit\\] TEST (.*)\\.(.*Test) FAILED");

    @Override
    public void process(Chat chat, User from, String buildCommandArguments) {
        BuildInfoDto repository = database.getAvailableRepository(buildCommandArguments.trim().split(" ")[0], from.id());

        if (repository == null) {
            defaultBuildCommand.process(chat, from, "error.command.build.common.wrongTeam");
            return;
        }

        log.info(from.username() + " is getting status build for team: " + repository.getRepoName());
        JenkinsInfoDto jenkinsInfo = repository.getJenkinsInfo();

        JenkinsBuildDetails currentBuildDetails = cliProcessor.getCurrentBuildJenkinsBuildDetails(repository.getJenkinsInfo(), failedTestsCount);
        JenkinsBuildDetails lastBuildDetails = cliProcessor.getPreviousBuildJenkinsBuildDetails(repository.getJenkinsInfo());

        String failedTestsOutputWithLinks = convertFailedTestsOutputToLinks(currentBuildDetails.getTopFailedTests(), jenkinsInfo);
        if (failedTestsOutputWithLinks.trim().isEmpty()) {
            failedTestsOutputWithLinks = "N/A";
        }

        bot.sendI18nMessage(chat, TelegramBotWrapper.MessageInfo.builder()
                        .messageKey("message.command.build.status.repo")
                        .messageArgs(new Object[] { repository.getRepoName(),
                                currentBuildDetails.getBuildStatus(),
                                bot.getI18nMessage(currentBuildDetails.getBuildStatus().getMessageKey()),
                                currentBuildDetails.getRunTestsCount(),
                                lastBuildDetails.getRunTestsCount(),
                                currentBuildDetails.getFailedTestsCapacity(),
                                currentBuildDetails.getFailedTestsCount(),
                                failedTestsOutputWithLinks})
                        .parseMode(ParseMode.Markdown)
                .build());
        defaultBuildCommand.process(chat, from, "");
    }

    // convert
    // [junit] TEST com.liquent.insight.manager.assembly.test.AssemblyExportTest FAILED
    // to
    // - [%s](http://%s:7331/job/%s/ws/output/reports/TEST-%s.xml/*view*/)
    private String convertFailedTestsOutputToLinks(List<String> origin, JenkinsInfoDto jenkinsInfo) {
        String result = String.join("\n", origin);
        Matcher m = failedTestsPattern.matcher(result);
        if (m.find()) {
            result = m.replaceAll(
                    String.format("- [%s](http://%s:7331/job/%s/ws/output/reports/TEST-%s.xml/*view*/)",
                            "$2", jenkinsInfo.getDomain(), jenkinsInfo.getJobName(), "$1.$2"));
        }

        return result;
    }

    public BuildType getBuildType() {
        return BuildType.STATUS;
    }
}
