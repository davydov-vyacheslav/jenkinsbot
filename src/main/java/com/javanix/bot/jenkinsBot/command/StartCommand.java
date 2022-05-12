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
		bot.execute(new SetMyCommands(
				new BotCommand("/help", bot.getI18nMessage(from, "text.command.help")),
				new BotCommand("/cancel", bot.getI18nMessage(from, "text.command.cancel")),
				new BotCommand("/start", bot.getI18nMessage(from, "text.command.start")),
				new BotCommand("/healthcheck", bot.getI18nMessage(from, "text.command.healthcheck")),
				new BotCommand("/build", bot.getI18nMessage(from, "text.command.build")))
		);
	}

	@Override
	public String getCommandName() {
		return "/start";
	}
}
