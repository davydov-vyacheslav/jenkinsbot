package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class DefaultBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
		List<BuildInfoDto> availableRepositories = database.getAvailableRepositories(from.id());
		bot.execute(new SendMessage(chat.id(), "Build info main list").replyMarkup(buildMainMenuMarkup(availableRepositories)));
	}

	@Override
	public BuildType getBuildType() {
		return null;
	}
}
