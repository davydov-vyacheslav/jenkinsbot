package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UnknownCommand implements TelegramCommand {

    private final TelegramBotWrapper bot;

    @Override
    public void process(Chat chat, User from, String message) {
        bot.sendI18nMessage(chat, "message.command.unknown");
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
