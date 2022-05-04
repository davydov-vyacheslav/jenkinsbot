package com.javanix.bot.jenkinsBot.command.build.model;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// TODO: merge with other Context
// Context of User-based action during `/build` action processing
@Component
@RequiredArgsConstructor
public class UserBuildContext {

	private final UserService userService;
	private final TelegramBotWrapper bot;

	private void removeLastMessage(Chat chat, User from) {
		Integer lastMessageId = userService.getUser(from.id()).getBuildMenuLastMessageId();
		if (lastMessageId != null) {
			bot.execute(new DeleteMessage(chat.id(), lastMessageId));
		}
	}

	public void executeCommandAndSaveMessageId(Chat chat, User from, TelegramBotWrapper.MessageInfo messageInfo) {
		removeLastMessage(chat, from);
		SendResponse execute = bot.sendI18nMessage(from, chat, messageInfo);
		UserInfoDto user = userService.getUser(from.id());
		user.setUserName(from.username());
		user.setBuildMenuLastMessageId(execute.message().messageId());
		userService.saveUser(user);
	}

}
