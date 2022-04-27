package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
class DefaultInProgressCommand implements ProgressableCommand {
	@Override
	public boolean isInProgress(Long userId) {
		return false;
	}

	@Override
	public void cancelProgress(TelegramBot bot, Chat chat, User from) {
		bot.execute(new SendMessage(chat.id(), "No active command to cancel. I wasn't doing anything anyway. Zzzzz... (c)"));
	}

	@Override
	public void progress(TelegramBot bot, Chat chat, User from, String message) {

	}

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {
		bot.execute(new SendMessage(chat.id(), String.format("Okay, what does `%s` mean?", message)));
	}
}
