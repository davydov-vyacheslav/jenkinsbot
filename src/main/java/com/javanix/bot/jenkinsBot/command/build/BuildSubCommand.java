package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.Processable;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

public interface BuildSubCommand extends Processable {

    String ICON_PUBLIC = "\uD83C\uDF0E ";
    String ICON_PRIVATE = "\uD83D\uDD12 ";

    BuildType getBuildType();

    default InlineKeyboardMarkup generateBuildStatusKeyboard(List<BuildInfoDto> availableRepositories) {
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[(int)Math.ceil(availableRepositories.size() / 2.0)][2];
        for (int i = 0; i < availableRepositories.size(); i++) {
            BuildInfoDto buildInfoDto = availableRepositories.get(i);
            String repoName = (buildInfoDto.getIsPublic() ? ICON_PUBLIC : ICON_PRIVATE) + buildInfoDto.getRepoName();
            buttons[i/2][i%2] = new InlineKeyboardButton(repoName).callbackData("/build status " + buildInfoDto.getRepoName());
        }
        if (availableRepositories.size() % 2 == 1) {
            buttons[availableRepositories.size() / 2][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");
        }
        return new InlineKeyboardMarkup(buttons);
    }

}
