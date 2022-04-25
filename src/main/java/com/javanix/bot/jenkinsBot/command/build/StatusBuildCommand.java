package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.database.BuildRepository;
import com.javanix.bot.jenkinsBot.database.DatabaseSource;
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
    private final DatabaseSource database;
    private static final int failedTestsCount = 20;
    private static final Pattern failedTestsPattern = Pattern.compile(".*\\[junit\\] TEST (.*)\\.(.*Test) FAILED");

    public void process(TelegramBot bot, Message message, String buildCommandArguments) {
        BuildRepository repository = database.getRepositoryByNameIgnoreCase(buildCommandArguments.trim().split(" ")[0]);

        if (repository == null) {
            List<BuildRepository> availableRepositories = database.getAvailableRepositories(message.from().id());
            InlineKeyboardMarkup inlineKeyboard = generateKeyboard(availableRepositories);
            bot.execute(new SendMessage(message.chat().id(), "Wrong team. Browse list of all team in /help").replyMarkup(inlineKeyboard));
            return;
        }

        log.info("Getting status build for team: " + repository.getRepoName());

        String statusFormatString = "Build status for %s team:\n" +
                "Run tests: %d (of approximately %d)\n" +
                "Top %d Failed tests (of %d): \n" +
                "%s";

        String failedTestsOutput = cliProcessor.getFailedTests(repository, failedTestsCount);
        if (failedTestsOutput.trim().isEmpty()) {
            failedTestsOutput = "N/A";
        }

        String failedTestsOutputWithLinks = convertFailedTestsOutputToLinks(failedTestsOutput, repository);

        bot.execute(new SendMessage(message.chat().id(),
                String.format(statusFormatString, repository.getRepoName(),
                        cliProcessor.getCurrentRunTestsCount(repository),
                        cliProcessor.getLastRunTestsCount(repository),
                        failedTestsCount,
                        cliProcessor.getFailedTestsCount(repository),
                        failedTestsOutputWithLinks))
                .parseMode(ParseMode.Markdown));
    }

    // convert
    // [junit] TEST com.liquent.insight.manager.assembly.test.AssemblyExportTest FAILED
    // to
    // - [%s](http://%s:7331/job/%s/ws/output/reports/TEST-%s.xml/*view*/)
    private String convertFailedTestsOutputToLinks(String origin, BuildRepository repository) {
        String result = origin;
        Matcher m = failedTestsPattern.matcher(origin);
        if (m.find()) {
            result = m.replaceAll(
                    String.format("- [%s](http://%s:7331/job/%s/ws/output/reports/TEST-%s.xml/*view*/)",
                            "$2", repository.getJenkinsDomain(), repository.getJobName(), "$1.$2"));
        }

        return result;
    }

    public BuildType getBuildType() {
        return BuildType.STATUS;
    }
}
