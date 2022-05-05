package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

public interface UserEntityContext {

	void executeCommandAndSaveMessageId(Chat chat, User from, TelegramBotWrapper.MessageInfo messageInfo);
}
