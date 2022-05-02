package com.javanix.bot.jenkinsBot.command.build.model;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// Context of User-based action during `/build` action processing
@Component
@RequiredArgsConstructor
public class UserBuildContext {

	// TODO: store in mongoDB ?
	private final Map<Long, Integer> buildMenuLastMessageId = new HashMap<>();
	private final TelegramBotWrapper bot;

	private void removeLastMessage(Chat chat, User from) {
		Integer lastMessageId = buildMenuLastMessageId.get(from.id());
		if (lastMessageId != null) {
			bot.execute(new DeleteMessage(chat.id(), lastMessageId));
		}
	}

	public void executeCommandAndSaveMessageId(Chat chat, User from, TelegramBotWrapper.MessageInfo messageInfo) {
		removeLastMessage(chat, from);
		SendResponse execute = bot.sendI18nMessage(chat, messageInfo);
		buildMenuLastMessageId.put(from.id(), execute.message().messageId());
	}

}
