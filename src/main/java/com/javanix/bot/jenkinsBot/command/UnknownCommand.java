package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
class UnknownCommand implements TelegramCommand {
    @Override
    public void process(TelegramBot bot, Chat chat, User from, String message) {
        bot.execute(new SendMessage(chat.id(), "Unknown command. Press /help to see list of all commands"));
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
