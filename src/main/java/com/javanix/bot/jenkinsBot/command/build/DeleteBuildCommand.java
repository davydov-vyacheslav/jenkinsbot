package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.database.BuildRepository;
import com.javanix.bot.jenkinsBot.database.DatabaseSource;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteBuildCommand implements BuildSubCommand {

    private final DatabaseSource database;

    @Override
    public void process(TelegramBot bot, Message message, String buildCommandArguments) {
        BuildRepository repository = database.getRepositoryByNameIgnoreCase(buildCommandArguments.trim().split(" ")[0]);

        if (repository == null || !repository.getCreatorId().equals(message.from().id())) {

            List<BuildRepository> availableRepositories = database.getOwnedRepositories(message.from().id());

            InlineKeyboardMarkup inlineKeyboard = generateKeyboard(availableRepositories);
            bot.execute(new SendMessage(message.chat().id(), "Wrong repo. You can delete only owned repository.").replyMarkup(inlineKeyboard));
            return;
        }

        database.removeRepo(repository);
        String statusFormatString = "Repository %s has been removed";
        bot.execute(new SendMessage(message.chat().id(), String.format(statusFormatString, repository.getRepoName())));
    }


    public BuildType getBuildType() {
        return BuildType.DELETE;
    }

}
