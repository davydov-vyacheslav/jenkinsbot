package com.javanix.bot.jenkinsBot;

import com.javanix.bot.jenkinsBot.core.service.UserService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TelegramBotWrapper {

	private final TelegramBot bot;
	private final MessageSource messageSource;
	private final UserService userService;

	public void setUpdatesListener(UpdatesListener listener) {
		bot.setUpdatesListener(listener);
	}

	public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
		return bot.execute(request);
	}

	public SendResponse sendI18nMessage(User from, Chat chat, MessageInfo messageInfo) {
		return this.execute(
				new SendMessage(
						chat.id(),
						getI18nMessage(from, messageInfo.messageKey, messageInfo.messageArgs))
						.replyMarkup(messageInfo.keyboard == null ? new InlineKeyboardMarkup(new InlineKeyboardButton[0]) : messageInfo.keyboard)
						.parseMode(messageInfo.parseMode == null ? ParseMode.HTML : messageInfo.parseMode));
	}

	public SendResponse sendI18nMessage(User from, Chat chat, String messageKey) {
		return sendI18nMessage(from, chat, MessageInfo.builder().messageKey(messageKey).build());
	}

	public String getI18nMessage(User from, String messageKey) {
		return getI18nMessage(from, messageKey, null);
	}

	public String getI18nMessage(User from, String messageKey, Object[] messageArgs) {
		Locale locale = userService.getUserLocale(from.id());
		return messageSource.getMessage(messageKey, messageArgs, messageKey, locale);
	}

	@Builder
	@Data
	public static class MessageInfo {
		private String messageKey;
		private Object[] messageArgs;
		private InlineKeyboardMarkup keyboard;
		private ParseMode parseMode;
	}

}
