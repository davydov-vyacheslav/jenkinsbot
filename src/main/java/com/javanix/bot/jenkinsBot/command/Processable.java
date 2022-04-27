package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

public interface Processable {
	void process(TelegramBot bot, Chat chat, User from, String message);
}
