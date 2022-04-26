package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class HelpCommand implements TelegramCommand {

	@Override
	public void process(TelegramBot bot, Message message) {

		try {
			bot.execute(new SendMessage(message.chat().id(),
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
