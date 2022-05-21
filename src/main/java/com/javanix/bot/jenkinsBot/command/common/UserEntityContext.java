package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class UserEntityContext {

	private final UserService userService;
	private final TelegramBotWrapper bot;

	private void removeLastMessage(UserInfoDto user, Chat chat, EntityType entityType) {
		Integer lastMessageId = user.getLastMessageIdMap().get(entityType);
		if (lastMessageId != null) {
			bot.execute(new DeleteMessage(chat.id(), lastMessageId));
		}
	}

	public Integer executeCommandAndSaveMessageId(Chat chat, User from, TelegramBotWrapper.MessageInfo messageInfo, EntityType entityType) {
		UserInfoDto user = userService.getUser(from.id());
		if (user.getLastMessageIdMap() == null) {
			user.setLastMessageIdMap(new HashMap<>());
		}
		removeLastMessage(user, chat, entityType);
		SendResponse execute = bot.sendI18nMessage(from, chat, messageInfo);
		user.setUserName(from.username());
		user.getLastMessageIdMap().put(entityType, execute.message().messageId());
		userService.saveUser(user);
		return execute.message().messageId();
	}
}
