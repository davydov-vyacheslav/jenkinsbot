package com.javanix.bot.jenkinsBot.core.model;

import java.util.Locale;

public enum LocaleType {
	EN(Locale.ENGLISH), RU(new Locale("ru"));

	private Locale locale;

	LocaleType(Locale locale) {
		this.locale = locale;
	}
}
