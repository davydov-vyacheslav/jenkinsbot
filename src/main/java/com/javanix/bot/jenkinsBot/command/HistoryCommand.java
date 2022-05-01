package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
class HistoryCommand implements TelegramCommand {

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {

		try {
			String changelog = IOUtils.toString(
					Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("CHANGELOG")),
					StandardCharsets.UTF_8);
			bot.execute(new SendMessage(chat.id(), changelog.substring(0, Math.min(changelog.length(), 2000))));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getCommandName() {
		return "/history";
	}
}
