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
class MyReposBuildCommand implements BuildSubCommand {

    private final BuildInfoService database;

    @Override
    public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
        List<BuildInfoDto> availableRepositories = database.getOwnedRepositories(from.id());
        InlineKeyboardMarkup inlineKeyboard = buildMyRepoListMarkup(availableRepositories);
        bot.execute(new SendMessage(chat.id(), "My Repositories").replyMarkup(inlineKeyboard));
    }


    public BuildType getBuildType() {
        return BuildType.MY_LIST;
    }

}
