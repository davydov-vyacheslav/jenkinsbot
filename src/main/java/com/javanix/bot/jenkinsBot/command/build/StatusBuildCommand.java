package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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
public class StatusBuildCommand implements BuildSubCommand {

    private final CliProcessor cliProcessor;
    private final BuildInfoService database;
    private static final int failedTestsCount = 20;
    private static final Pattern failedTestsPattern = Pattern.compile(".*\\[junit\\] TEST (.*)\\.(.*Test) FAILED");

    public void process(TelegramBot bot, Message message, String buildCommandArguments) {
        BuildInfoDto repository = database.getAvailableRepository(buildCommandArguments.trim().split(" ")[0], message.from().id());

        if (repository == null) {
            List<BuildInfoDto> availableRepositories = database.getAvailableRepositories(message.from().id());
            InlineKeyboardMarkup inlineKeyboard = generateKeyboard(availableRepositories);
            // TODO: ? buttons instead of  keyboard?
            bot.execute(new SendMessage(message.chat().id(), "Wrong team. Please choose correct one").replyMarkup(inlineKeyboard));
            return;
        }

        log.info("Getting status build for team: " + repository.getRepoName());
        JenkinsInfoDto jenkinsInfo = repository.getJenkinsInfo();

        String statusFormatString = "Build status for %s team:\n" +
                "Run tests: %d (of approximately %d)\n" +
                "Top %d Failed tests (of %d): \n" +
                "%s";

        String failedTestsOutput = cliProcessor.getFailedTests(repository.getJenkinsInfo(), failedTestsCount);
        if (failedTestsOutput.trim().isEmpty()) {
            failedTestsOutput = "N/A";
        }

        String failedTestsOutputWithLinks = convertFailedTestsOutputToLinks(failedTestsOutput, jenkinsInfo);

        bot.execute(new SendMessage(message.chat().id(),
                String.format(statusFormatString, repository.getRepoName(),
                        cliProcessor.getCurrentRunTestsCount(jenkinsInfo),
                        cliProcessor.getLastRunTestsCount(jenkinsInfo),
                        failedTestsCount,
                        cliProcessor.getFailedTestsCount(jenkinsInfo),
                        failedTestsOutputWithLinks))
                .parseMode(ParseMode.Markdown));
    }

    // convert
    // [junit] TEST com.liquent.insight.manager.assembly.test.AssemblyExportTest FAILED
    // to
    // - [%s](http://%s:7331/job/%s/ws/output/reports/TEST-%s.xml/*view*/)
    private String convertFailedTestsOutputToLinks(String origin, JenkinsInfoDto jenkinsInfo) {
        String result = origin;
        Matcher m = failedTestsPattern.matcher(origin);
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
