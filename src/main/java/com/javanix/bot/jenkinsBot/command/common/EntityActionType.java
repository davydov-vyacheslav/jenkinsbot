package com.javanix.bot.jenkinsBot.command.common;

import java.util.Arrays;

public enum EntityActionType {
	ADD, STATUS, DELETE, MY_LIST, EDIT, ADD_REFERENCE;

	public static EntityActionType of(String value) {
		return Arrays.stream(values())
				.filter(entityActionType -> entityActionType.toString().equalsIgnoreCase(value))
				.findAny().orElse(null);
	}
}
