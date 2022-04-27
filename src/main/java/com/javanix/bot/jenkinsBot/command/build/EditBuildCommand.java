package com.javanix.bot.jenkinsBot.command.build;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
class EditBuildCommand implements BuildSubCommand {

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {
		bot.execute(new SendMessage(chat.id(), "Editing ... " + message));
		// TODO: check permissions
	}

	@Override
	public BuildType getBuildType() {
		return BuildType.EDIT;
	}

	// TODO: implement me

	// TODO: repoName cant be updated
}
