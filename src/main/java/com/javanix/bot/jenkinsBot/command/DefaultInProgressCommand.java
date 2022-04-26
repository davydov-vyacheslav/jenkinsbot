package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
class DefaultInProgressCommand implements ProgressableCommand {
	@Override
	public boolean isInProgress(Long userId) {
		return false;
	}

	@Override
	public void process(TelegramBot bot, Message message) {
		bot.execute(new SendMessage(message.chat().id(), String.format("Okay, what does `%s` mean?", message.text())));
	}
}
