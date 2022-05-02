package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

public interface Processable {
	void process(Chat chat, User from, String message);
}
