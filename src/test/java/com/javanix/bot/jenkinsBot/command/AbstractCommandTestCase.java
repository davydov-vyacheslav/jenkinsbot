package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.CacheService;
import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.ArgumentMatchers.any;

public abstract class AbstractCommandTestCase {

	@MockBean
	protected TelegramBotWrapper bot;

	@MockBean
	protected Chat chat;

	@MockBean
	protected SendResponse sendResponse;

	@MockBean
	private CacheService cacheService;

	@MockBean
	private UserService userService;

	@MockBean
	private HealthCheckService healthCheckService;

	@Autowired
	protected CommonCommandFactory factory;

	@BeforeEach
	public void setup() {
		// FIXME: make bean?
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(userService.getUser(any())).thenReturn(UserInfoDto.emptyEntityBuilder().build());
		Mockito.when(bot.getI18nMessage(any(), any())).then(returnsArgAt(ReturnsArgumentAt.LAST_ARGUMENT));
		Mockito.when(bot.getI18nMessage(any(), any(), any())).then(invocation -> {
			String key = invocation.getArgument(1);
			if (key.equals("message.command.build.common.repoInfo.prefix")) {
				key = "Current repository info: \\n{0}";
			} else if (key.equals("button.build.setFieldValue")) {
				key = "Set `{0}`";
			}
			return new MessageFormat(key).format(invocation.getArgument(2));
		});
	}

	protected List<InlineKeyboardButton> getInlineKeyboardButtons(TelegramBotWrapper.MessageInfo message) {
		InlineKeyboardMarkup reply_markup = message.getKeyboard();
		InlineKeyboardButton[][] buttons = reply_markup == null ? new InlineKeyboardButton[0][0] : reply_markup.inlineKeyboard();
		return Arrays.stream(buttons).flatMap(Arrays::stream).collect(Collectors.toList());
	}

}
