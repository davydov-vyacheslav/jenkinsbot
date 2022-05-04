package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class HelpCommand implements TelegramCommand {

	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String message) {
		bot.sendI18nMessage(from, chat, "message.command.help");
	}

	@Override
	public String getCommandName() {
		return "/help";
	}
}
