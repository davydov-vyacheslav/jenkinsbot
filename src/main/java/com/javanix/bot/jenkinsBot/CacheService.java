package com.javanix.bot.jenkinsBot;

import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Log4j2
@Component
@RequiredArgsConstructor
public class CacheService {

	private final UserService userService;

	@Cacheable(value = "userLocale")
	public Locale getUserLocale(Long userId) {
		log.info(">> User locale triggered: " + userId);
		return userService.getUser(userId).getLocale().getLocale();
	}

	@CachePut(value = "userLocale", key = "#userId")
	public Locale updateUserLocale(Long userId, Locale locale) {
		UserInfoDto user = userService.getUser(userId);
		user.setLocale(LocaleType.of(locale.getLanguage()));
		userService.saveUser(user);
		return locale;
	}

}
