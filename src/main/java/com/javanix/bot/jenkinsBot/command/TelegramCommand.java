package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public interface TelegramCommand {

    void process(TelegramBot bot, Message message);

    // TODO: make List<String> getCommandNames()
    String getCommandName();
}
