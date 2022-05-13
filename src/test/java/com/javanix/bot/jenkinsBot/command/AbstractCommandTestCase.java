package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.CacheService;
import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import com.javanix.bot.jenkinsBot.database.DatabaseFactory;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
public abstract class AbstractCommandTestCase {

	protected static final String ENTITY_NAME = "Entity Name";
	protected static final String ENTITY_NAME_2 = "Another Entity Name";
	protected static final String ENTITY_URL = "https://domain.com/";
	protected static final String ENTITY_URL_2 = "https://another.com/";

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
	protected HealthCheckService healthCheckService;

	@MockBean
	protected DatabaseFactory databaseFactory;

	@MockBean
	protected BuildInfoService buildInfoService;

	@Autowired
	protected CommonCommandFactory factory;

	private static final Map<String, String> I18N_MAP = new ConcurrentHashMap<String, String>() {{
		put("message.command.build.common.status.prefix", "Current repository info: \\n{0}");
		put("message.command.healthcheck.common.status.prefix", "Current Endpoint info: \\n{0}");
		put("button.common.setFieldValue", "Set `{0}`");
	}};

	@BeforeEach
	public void setup() {
		// FIXME: make bean?
		Mockito.when(sendResponse.message()).thenReturn(new Message());
		Mockito.when(userService.getUser(any())).thenReturn(UserInfoDto.emptyEntityBuilder().build());
		Mockito.when(bot.getI18nMessage(any(), any())).then(returnsArgAt(ReturnsArgumentAt.LAST_ARGUMENT));
		Mockito.when(bot.getI18nMessage(any(), any(), any())).then(invocation -> {
			String key = invocation.getArgument(1);
			key = I18N_MAP.getOrDefault(key, key);
			return new MessageFormat(key).format(invocation.getArgument(2));
		});
	}

	protected List<InlineKeyboardButton> getInlineKeyboardButtons(TelegramBotWrapper.MessageInfo message) {
		InlineKeyboardMarkup replyMarkup = message.getKeyboard();
		InlineKeyboardButton[][] buttons = replyMarkup == null ? new InlineKeyboardButton[0][0] : replyMarkup.inlineKeyboard();
		return Arrays.stream(buttons).flatMap(Arrays::stream).collect(Collectors.toList());
	}

}
