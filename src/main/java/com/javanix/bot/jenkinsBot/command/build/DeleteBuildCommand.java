package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
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
	private final UserEntityContext userContext;

	@Override
	public void process(Chat chat, User from, String repoName) {
		if (database.removeEntity(from.id(), repoName)) {
			userContext.executeCommandAndSaveMessageId(chat, from, TelegramBotWrapper.MessageInfo.builder()
					.messageKey("message.command.build.delete.processed")
					.messageArgs(new Object[] { repoName })
					.build(), EntityType.BUILD_INFO);
			defaultBuildCommand.process(chat, from, "");
		} else {
			myReposBuildCommand.process(chat, from, "error.command.build.delete");
		}
	}

	@Override
	public EntityActionType getCommandType() {
		return EntityActionType.DELETE;
	}

}
