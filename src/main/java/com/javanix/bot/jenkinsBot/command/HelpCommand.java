package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
class HelpCommand implements TelegramCommand {

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {

		try {
			bot.execute(new SendMessage(chat.id(),
							String.join("\n", IOUtils.readLines(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("README.md")), StandardCharsets.UTF_8)))
					.parseMode(ParseMode.Markdown));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getCommandName() {
		return "/help";
	}
}
