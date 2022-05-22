package com.javanix.bot.jenkinsBot;

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

	@Cacheable("userLocale")
	public Locale getUserLocale(Long userId) {
		log.info(">> User locale triggered: " + userId);
		return null;
	}

	@CachePut(value = "userLocale", key = "#userId")
	public Locale updateUserLocale(Long userId, Locale locale) {
		return locale;
	}

}
