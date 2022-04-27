package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

// Command that contains states for question-answers actions
public interface ProgressableCommand extends Processable {

	boolean isInProgress(Long userId);

	void cancelProgress(TelegramBot bot, Chat chat, User from);

	void progress(TelegramBot bot, Chat chat, User from, String message);
}
