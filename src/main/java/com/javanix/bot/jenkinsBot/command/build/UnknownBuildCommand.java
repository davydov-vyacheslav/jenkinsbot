package com.javanix.bot.jenkinsBot.command.build;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
class UnknownBuildCommand implements BuildSubCommand {
    @Override
    public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
        InlineKeyboardButton[] buttons = new InlineKeyboardButton[BuildType.values().length];
        for (int i = 0; i < BuildType.values().length; i++) {
            buttons[i] = new InlineKeyboardButton(BuildType.values()[i].toString()).callbackData("/build " + BuildType.values()[i]);
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(buttons);
        bot.execute(new SendMessage(chat.id(), "Wrong operation. Choose one from list").replyMarkup(inlineKeyboard));
    }

    @Override
    public BuildType getBuildType() {
        return null;
    }
}
