package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
class HistoryCommand implements TelegramCommand {

	private final TelegramBotWrapper bot;

	@Override
	@SneakyThrows
	public void process(Chat chat, User from, String message) {
		String changelog = IOUtils.toString(
				Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("CHANGELOG.md")),
				StandardCharsets.UTF_8);
		bot.execute(new SendMessage(chat.id(), changelog.substring(0, Math.min(changelog.length(), 2000))));
	}

	@Override
	public String getCommandName() {
		return "/history";
	}
}
