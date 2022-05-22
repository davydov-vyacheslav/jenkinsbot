package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;

import java.util.Locale;

public interface UserService {

	UserInfoDto getUser(Long telegramId);

	void saveUser(UserInfoDto user);

	Locale getUserLocale(long telegramId);

	void updateUserLocale(long telegramId, LocaleType locale);

}
