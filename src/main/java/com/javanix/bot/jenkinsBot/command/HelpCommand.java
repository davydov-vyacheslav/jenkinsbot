package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements TelegramCommand {

    @Override
    public void process(TelegramBot bot, Message message) {

        bot.execute(new SendMessage(message.chat().id(),
                "List of commands: \n" +
                "* /help - This help message\n" +
                "* /build <type={add,delete,status}> - build management and getting actual info"));
    }

    @Override
    public String getCommandName() {
        return "/help";
    }
}
