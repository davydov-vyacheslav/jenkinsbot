package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DefaultInProgressCommand implements ProgressableCommand {

	private final TelegramBotWrapper bot;

	@Override
	public boolean isInProgress(Long userId) {
		return false;
	}

	@Override
	public void cancelProgress(Chat chat, User from) {
		bot.sendI18nMessage(from, chat, "message.command.defaultInProgress.cancel");
	}

	@Override
	public void progress(Chat chat, User from, String message) {
		bot.sendI18nMessage(from, chat, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command.defaultInProgress.progress")
				.messageArgs(new Object[] { message })
				.build());
	}

	@Override
	public void process(Chat chat, User from, String message) {
		bot.sendI18nMessage(from, chat, TelegramBotWrapper.MessageInfo.builder()
				.messageKey("message.command.defaultInProgress.process")
				.messageArgs(new Object[] { message })
				.build());
	}
}
