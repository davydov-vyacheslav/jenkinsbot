package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DeleteBuildCommand implements BuildSubCommand {

	private final BuildInfoService database;
	private final DefaultBuildCommand defaultBuildCommand;
	private final MyReposBuildCommand myReposBuildCommand;
	private final UserBuildContext userContext;

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String buildCommandArguments) {
// FIXME: grace: exception based?
		database.getOwnedRepository(buildCommandArguments.trim().split(" ")[0], from.id())
				.map(repository -> {
					database.removeRepo(repository.getRepoName());
					userContext.executeCommandAndSaveMessageId(bot, chat, from,
							new SendMessage(chat.id(), String.format("Repository %s has been removed", repository.getRepoName())));
					defaultBuildCommand.process(bot, chat, from, "");
					return null;
				})
				.orElseGet(() -> {
					myReposBuildCommand.process(bot, chat, from, "Wrong repo. You can delete only owned repository.");
					return null;
				});
	}

	public BuildType getBuildType() {
		return BuildType.DELETE;
	}

}
