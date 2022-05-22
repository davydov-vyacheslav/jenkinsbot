package com.javanix.bot.jenkinsBot.core.model;

import java.util.Arrays;
import java.util.Locale;

public enum LocaleType {
	EN(Locale.forLanguageTag("")),
	RU(Locale.forLanguageTag("ru")),
	UK(Locale.forLanguageTag("uk"));

	private final Locale locale;

	LocaleType(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	public static LocaleType of(String localeType) {
		return Arrays.stream(values())
				.filter(lt -> lt.toString().equalsIgnoreCase(localeType))
				.findAny()
				.orElse(EN);
	}
}
