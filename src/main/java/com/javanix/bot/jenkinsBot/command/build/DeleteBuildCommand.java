package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class DeleteBuildCommand implements BuildSubCommand {

    private final BuildInfoService database;

    @Override
    public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
        BuildInfoDto repository = database.getOwnedRepository(buildCommandArguments.trim().split(" ")[0], from.id());

        if (repository == null) {
            List<BuildInfoDto> availableRepositories = database.getOwnedRepositories(from.id());
            InlineKeyboardMarkup inlineKeyboard = buildMyRepoListMarkup(availableRepositories);
            bot.execute(new SendMessage(chat.id(), "Wrong repo. You can delete only owned repository.").replyMarkup(inlineKeyboard));
            return;
        }

        database.removeRepo(repository.getRepoName());
        String statusFormatString = "Repository %s has been removed";
        bot.execute(new SendMessage(chat.id(), String.format(statusFormatString, repository.getRepoName())));
    }


    public BuildType getBuildType() {
        return BuildType.DELETE;
    }

}
