package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;

public interface UserService {

	UserInfoDto getUser(Long telegramId);

	void saveUser(UserInfoDto user);

}
