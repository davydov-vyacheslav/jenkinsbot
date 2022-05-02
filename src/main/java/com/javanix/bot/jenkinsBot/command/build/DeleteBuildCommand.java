package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.command.build.model.UserBuildContext;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
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
	public void process(Chat chat, User from, String buildCommandArguments) {
// FIXME: grace: exception based?
		database.getOwnedRepository(buildCommandArguments.trim().split(" ")[0], from.id())
				.map(repository -> {
					database.removeRepo(repository.getRepoName());
					userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
							.messageKey("message.command.build.delete.processed")
							.messageArgs(new Object[] { repository.getRepoName() })
							.build());
					defaultBuildCommand.process(chat, from, "");
					return repository;
				})
				.orElseGet(() -> {
					myReposBuildCommand.process(chat, from, "error.command.build.delete");
					return null;
				});
	}

	public BuildType getBuildType() {
		return BuildType.DELETE;
	}

}
