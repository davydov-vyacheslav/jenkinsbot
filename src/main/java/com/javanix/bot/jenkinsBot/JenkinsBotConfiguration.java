package com.javanix.bot.jenkinsBot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class JenkinsBotConfiguration {

	@Value("${bot.token}")
	private String botToken;

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("userLocale", "patterns");
	}

	@Bean
	public TelegramBot telegramBot() {
		return new TelegramBot(botToken);
	}

}
