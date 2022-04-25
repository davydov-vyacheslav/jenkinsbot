package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.build.add.RepoAddInformation;
import com.javanix.bot.jenkinsBot.command.build.add.StateType;
import com.javanix.bot.jenkinsBot.database.BuildRepository;
import com.javanix.bot.jenkinsBot.database.DatabaseSource;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AddBuildCommand implements BuildSubCommand {
    private final Map<Long, RepoAddInformation> userAddBuildStates = new HashMap<>();

    private final DatabaseSource database;

    @Override
    public void process(TelegramBot bot, Message message, String buildCommandArguments) {
        Long currentId = message.from().id();

        if (!userAddBuildStates.containsKey(currentId)) {
            userAddBuildStates.put(currentId, new RepoAddInformation(StateType.INITIAL,
                    BuildRepository.builder()
                            .creatorId(currentId)
                            .creatorFullName(message.from().username())
                        .build()));
            bot.execute(new SendMessage(message.chat().id(), "Okay. Lets create new repository. Note: Storage - memory. Please follow instructions.")); // TODO: correct when sql storage implemented
        }


        if (buildCommandArguments.equalsIgnoreCase("/cancel")) {
            bot.execute(new SendMessage(message.chat().id(), "Ok. you cancelled. Buy"));
            userAddBuildStates.remove(currentId);
            return;
        }

        BuildRepository repo = userAddBuildStates.get(currentId).getRepo();
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
                "\n- jenkinsDomain: " + repo.getJenkinsDomain() +
                "\n- jenkinsUser: " + repo.getJenkinsUser() +
                "\n- jenkinsPassword: " + repo.getJenkinsPassword() +
                "\n- jobName: " + repo.getJobName() +
                "\n- isPublic: " + repo.getIsPublic());


        if (userAddBuildStates.get(currentId).getState() == StateType.COMPLETED) {
            database.addRepository(repo);
            userAddBuildStates.remove(currentId);
            bot.execute(new SendMessage(message.chat().id(), String.format("Use `/build status %s` to get build status", repo.getRepoName())));
        } else {

            bot.execute(new SendMessage(message.chat().id(), "P.S. Press cancel button to cancel creation any time"));

            // TODO: do we need this?
            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("Continue").switchInlineQueryCurrentChat("/build add "),
                    new InlineKeyboardButton("Cancel").switchInlineQueryCurrentChat("/build add /cancel")
            );
            sendMessage = sendMessage.replyMarkup(inlineKeyboard);
        }

        bot.execute(sendMessage);

    }

    public BuildType getBuildType() {
        return BuildType.ADD;
    }
}
