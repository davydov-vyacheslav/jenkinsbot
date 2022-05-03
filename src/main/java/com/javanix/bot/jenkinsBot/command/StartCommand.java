package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class StartCommand implements TelegramCommand {

	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String message) {
		// TODO: commands as i18n
		bot.execute(new SetMyCommands(
				new BotCommand("/help", "Show help message"),
				new BotCommand("/cancel", "Cancel any in-progress action (e.g. creation)"),
				new BotCommand("/start", "You already did that ;)"),
				new BotCommand("/healthcheck", "HealthCheck for external services"),
				new BotCommand("/build", "Build processing"))
		);
	}

	@Override
	public String getCommandName() {
		return "/start";
	}
}
