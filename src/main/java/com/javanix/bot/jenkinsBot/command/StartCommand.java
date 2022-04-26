package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements TelegramCommand {

	@Override
	public void process(TelegramBot bot, Message message) {
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
