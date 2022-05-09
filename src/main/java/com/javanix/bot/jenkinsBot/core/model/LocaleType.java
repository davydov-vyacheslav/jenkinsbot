package com.javanix.bot.jenkinsBot.core.model;

import java.util.Locale;

public enum LocaleType {
	EN(Locale.ENGLISH), RU(new Locale("ru"));

	private final Locale locale;

	LocaleType(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	public static LocaleType of(String localeType) {
		LocaleType result = EN;
		if ("ru".equalsIgnoreCase(localeType)) {
			result = RU;
		}
		return result;
	}
}
