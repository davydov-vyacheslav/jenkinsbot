package com.javanix.bot.jenkinsBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TelegramBotWrapper {

	@Value("${bot.token}")
	private String botToken;

	private TelegramBot bot;

	private final MessageSource messageSource;

	@PostConstruct
	public void init() {
		bot = new TelegramBot(botToken);
	}

	public void setUpdatesListener(UpdatesListener listener) {
		bot.setUpdatesListener(listener);
	}

	public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
		return bot.execute(request);
	}

	// TODO: replace by l10n for localization

	public SendResponse sendI18nMessage(Chat chat, MessageInfo messageInfo) {
		return this.execute(
				new SendMessage(
						chat.id(),
						messageSource.getMessage(messageInfo.messageKey, messageInfo.messageArgs, messageInfo.messageKey, Locale.getDefault()))
						.replyMarkup(messageInfo.keyboard == null ? new InlineKeyboardMarkup(new InlineKeyboardButton[0]) : messageInfo.keyboard)
						.parseMode(messageInfo.parseMode == null ? ParseMode.HTML : messageInfo.parseMode));
	}

	public SendResponse sendI18nMessage(Chat chat, String messageKey) {
		return sendI18nMessage(chat, MessageInfo.builder().messageKey(messageKey).build());
	}

	// FIXME: hack?

	public String getI18nMessage(String messageKey) {
		return messageSource.getMessage(messageKey, null, messageKey, Locale.getDefault());
	}

	public String getI18nMessage(String messageKey, Object[] messageArgs) {
		return messageSource.getMessage(messageKey, messageArgs, messageKey, Locale.getDefault());
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
