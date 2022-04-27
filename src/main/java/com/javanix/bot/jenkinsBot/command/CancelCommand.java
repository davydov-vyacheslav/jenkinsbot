package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
class CancelCommand implements TelegramCommand {

	private final Set<ProgressableCommand> progressableCommands;
	private final DefaultInProgressCommand defaultCommand;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {
		progressableCommands.stream()
				.filter(progressableCommand -> progressableCommand.isInProgress(from.id()))
				.findFirst()
				.orElse(defaultCommand)
				.stopProgress(bot, chat, from);
	}

	@Override
	public String getCommandName() {
		return "/cancel";
	}
}
