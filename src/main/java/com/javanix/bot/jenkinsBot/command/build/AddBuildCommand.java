package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.ProgressableCommand;
import com.javanix.bot.jenkinsBot.command.build.add.RepoAddInformation;
import com.javanix.bot.jenkinsBot.command.build.add.StateType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AddBuildCommand implements BuildSubCommand, ProgressableCommand {
    private final Map<Long, RepoAddInformation> userAddBuildStates = new HashMap<>();

    private final BuildInfoService database;

    @Override
    public void process(TelegramBot bot, Message message, String buildCommandArguments) {
        Long currentId = message.from().id();

        if (!userAddBuildStates.containsKey(currentId)) {
            userAddBuildStates.put(currentId, new RepoAddInformation(StateType.INITIAL,
                    BuildInfoDto.builder()
                            .jenkinsInfo(JenkinsInfoDto.builder().build())
                            .creatorId(currentId)
                            .creatorFullName(message.from().username())
                        .build()));
            bot.execute(new SendMessage(message.chat().id(), "Okay. Lets create new repository. Please follow instructions."));
        }


        if (buildCommandArguments.equalsIgnoreCase("!cancel")) {
            bot.execute(new SendMessage(message.chat().id(), "Ok. you cancelled. Bye"));
            userAddBuildStates.remove(currentId);
            return;
        }

        BuildInfoDto repo = userAddBuildStates.get(currentId).getRepo();
        StateType currentState = userAddBuildStates.get(currentId).getState();
        StateType nextState = currentState.getNextState();

        if (currentState.isValid(database, buildCommandArguments)) {
            currentState.performUpdate(repo, buildCommandArguments);
            bot.execute(new SendMessage(message.chat().id(), nextState.getMessage()));
            userAddBuildStates.get(currentId).setState(nextState);
        } else {
            bot.execute(new SendMessage(message.chat().id(), "Wrong value. Try again"));
        }

        SendMessage sendMessage = new SendMessage(message.chat().id(), "Current repository info:" +
                "\n- repoName: " + repo.getRepoName() +
                "\n- jenkinsDomain: " + repo.getJenkinsInfo().getDomain() +
                "\n- jenkinsUser: " + repo.getJenkinsInfo().getUser() +
                "\n- jenkinsPassword: " + repo.getJenkinsInfo().getPassword() +
                "\n- jobName: " + repo.getJenkinsInfo().getJobName() +
                "\n- isPublic: " + repo.getIsPublic());


        if (userAddBuildStates.get(currentId).getState() == StateType.COMPLETED) {
            database.addRepository(repo);
            userAddBuildStates.remove(currentId);

            // TODO: ? buttons instead keyboard?
            List<BuildInfoDto> availableRepositories = database.getAvailableRepositories(message.from().id());
            InlineKeyboardMarkup inlineKeyboard = generateKeyboard(availableRepositories);
            bot.execute(new SendMessage(message.chat().id(), "Select build to get build status").replyMarkup(inlineKeyboard));

        } else {
            bot.execute(new SendMessage(message.chat().id(), "P.S. Press `!cancel` to cancel creation any time"));
        }

        bot.execute(sendMessage);

    }

    @Override
    public boolean isInProgress(Long userId) {
        return userAddBuildStates.containsKey(userId);
    }

    @Override
    public void process(TelegramBot bot, Message message) {
        process(bot, message, message.text());
    }

    public BuildType getBuildType() {
        return BuildType.ADD;
    }
}
