package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
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
    public void process(TelegramBot bot, Message message, String buildCommandArguments) {
        BuildInfoDto repository = database.getOwnedRepository(buildCommandArguments.trim().split(" ")[0], message.from().id());

        if (repository == null) {
            List<BuildInfoDto> availableRepositories = database.getOwnedRepositories(message.from().id());
            InlineKeyboardMarkup inlineKeyboard = generateBuildStatusKeyboard(availableRepositories);
            bot.execute(new SendMessage(message.chat().id(), "Wrong repo. You can delete only owned repository.").replyMarkup(inlineKeyboard));
            return;
        }

        database.removeRepo(repository.getRepoName());
        String statusFormatString = "Repository %s has been removed";
        bot.execute(new SendMessage(message.chat().id(), String.format(statusFormatString, repository.getRepoName())));
    }


    public BuildType getBuildType() {
        return BuildType.DELETE;
    }

}
