package com.javanix.bot.jenkinsBot.command.build.model;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// Context of User-based action during `/build` action processing
@Component
public class UserBuildContext {

	private final Map<Long, Integer> buildMenuLastMessageId = new HashMap<>();

	// TODO: store in mongoDB ?

	private void removeLastMessage(TelegramBot bot, Chat chat, User from) {
		Integer lastMessageId = buildMenuLastMessageId.get(from.id());
		if (lastMessageId != null) {
			bot.execute(new DeleteMessage(chat.id(), lastMessageId));
		}
	}

	public void executeCommandAndSaveMessageId(TelegramBot bot, Chat chat, User from, SendMessage request) {
		removeLastMessage(bot, chat, from);
		SendResponse execute = bot.execute(request);
		buildMenuLastMessageId.put(from.id(), execute.message().messageId());
	}

}
