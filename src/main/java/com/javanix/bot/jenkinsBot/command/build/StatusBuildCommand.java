package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.cli.JenkinsBuildDetails;
import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
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
    private static final int failedTestsCount = 20;
    private static final Pattern failedTestsPattern = Pattern.compile(".*\\[junit\\] TEST (.*)\\.(.*Test) FAILED");

    public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
        BuildInfoDto repository = database.getAvailableRepository(buildCommandArguments.trim().split(" ")[0], from.id());

        if (repository == null) {
            defaultBuildCommand.process(bot, chat, from, "Wrong team. Please choose correct one");
            return;
        }

        log.info(from.username() + " is getting status build for team: " + repository.getRepoName());
        JenkinsInfoDto jenkinsInfo = repository.getJenkinsInfo();

        String statusFormatString = "Build status for `%s` team:\n" +
                "Run tests: %d (of approximately %d)\n" +
                "Top %d Failed tests (of %d): \n" +
                "%s";

        JenkinsBuildDetails currentBuildDetails = cliProcessor.getCurrentBuildJenkinsBuildDetails(repository.getJenkinsInfo(), failedTestsCount);
        JenkinsBuildDetails lastBuildDetails = cliProcessor.getPreviousBuildJenkinsBuildDetails(repository.getJenkinsInfo());

        String failedTestsOutputWithLinks = convertFailedTestsOutputToLinks(currentBuildDetails.getTopFailedTests(), jenkinsInfo);
        if (failedTestsOutputWithLinks.trim().isEmpty()) {
            failedTestsOutputWithLinks = "N/A";
        }

        String buildStatus = String.format(statusFormatString, repository.getRepoName(),
                currentBuildDetails.getRunTestsCount(),
                lastBuildDetails.getRunTestsCount(),
                currentBuildDetails.getFailedTestsCapacity(),
                currentBuildDetails.getFailedTestsCount(),
                failedTestsOutputWithLinks);

        log.info(buildStatus);

        bot.execute(new SendMessage(chat.id(), buildStatus).parseMode(ParseMode.Markdown));
        defaultBuildCommand.process(bot, chat, from, "");
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
