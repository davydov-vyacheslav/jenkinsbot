package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommandTestCase {

	protected Object getText(InvocationOnMock invocation) {
		return ((SendMessage) invocation.getArgument(0)).getParameters().get("text");
	}

	protected List<InlineKeyboardButton> getInlineKeyboardButtons(InvocationOnMock invocation) {
		SendMessage message = invocation.getArgument(0);
		InlineKeyboardMarkup reply_markup = (InlineKeyboardMarkup) message.getParameters().get("reply_markup");
		InlineKeyboardButton[][] buttons = reply_markup == null ? new InlineKeyboardButton[0][0] : reply_markup.inlineKeyboard();
		return Arrays.stream(buttons).flatMap(Arrays::stream).collect(Collectors.toList());
	}

}
