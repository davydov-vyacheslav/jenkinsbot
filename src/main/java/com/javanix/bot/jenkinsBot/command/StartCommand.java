package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.springframework.stereotype.Component;

@Component
class StartCommand implements TelegramCommand {

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {
		bot.execute(new SetMyCommands(
				new BotCommand("/help", "Show help message"),
				new BotCommand("/start", "You already did that ;)"),
				new BotCommand("/build", "Build processing"))
		);
	}

	@Override
	public String getCommandName() {
		return "/start";
	}
}
