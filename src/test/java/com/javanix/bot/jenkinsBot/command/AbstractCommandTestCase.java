package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

public abstract class AbstractCommandTestCase {

	// FIXME: is that correct way? if so, why IDEA argue this?
	@MockBean
	public TelegramBotWrapper bot;

	@BeforeEach
	public void setup() {
		// FIXME: make bean?
		Mockito.when(bot.getI18nMessage(any())).then(returnsFirstArg());
		Mockito.when(bot.getI18nMessage(any(), any())).then(invocation -> {
			String key = invocation.getArgument(0);
			if (key.equals("message.command.build.common.repoInfo.prefix")) {
				key = "Current repository info: \\n{0}";
			} else if (key.equals("button.build.setFieldValue")) {
				key = "Set `{0}`";
			}
			return new MessageFormat(key).format(invocation.getArgument(1));
		});
	}

	protected List<InlineKeyboardButton> getInlineKeyboardButtons(TelegramBotWrapper.MessageInfo message) {
		InlineKeyboardMarkup reply_markup = message.getKeyboard();
		InlineKeyboardButton[][] buttons = reply_markup == null ? new InlineKeyboardButton[0][0] : reply_markup.inlineKeyboard();
		return Arrays.stream(buttons).flatMap(Arrays::stream).collect(Collectors.toList());
	}

}
