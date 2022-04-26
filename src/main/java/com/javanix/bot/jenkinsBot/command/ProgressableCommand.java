package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

// Command that contains states for question-answers actions
public interface ProgressableCommand {
	boolean isInProgress(Long userId);

	void process(TelegramBot bot, Message message);
}
