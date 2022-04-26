package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

public interface BuildSubCommand {

    void process(TelegramBot bot, Message message, String buildCommandArguments);

    BuildType getBuildType();

    default InlineKeyboardMarkup generateKeyboard(List<BuildInfoDto> availableRepositories) {
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[(int)Math.ceil(availableRepositories.size() / 2.0)][2];
        for (int i = 0; i < availableRepositories.size(); i++) {
            String repoName = availableRepositories.get(i).getRepoName();
            buttons[i/2][i%2] = new InlineKeyboardButton("Team: " + repoName).switchInlineQueryCurrentChat("/build status " + repoName);
        }
        if (availableRepositories.size() % 2 == 1) {
            buttons[availableRepositories.size() / 2][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");
        }
        return new InlineKeyboardMarkup(buttons);
    }

}
