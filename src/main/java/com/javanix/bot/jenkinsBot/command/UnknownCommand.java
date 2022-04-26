package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class UnknownCommand implements TelegramCommand {
    @Override
    public void process(TelegramBot bot, Message message) {
        bot.execute(new SendMessage(message.chat().id(), "Unknown command. Press /help to see list of all commands"));
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
