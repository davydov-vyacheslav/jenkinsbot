package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.core.model.Entity;

public interface EntityState<T extends Entity> {

	String getFieldKey();

	Object getValue(T entity);

	void updateField(T entity, String value);

	@FunctionalInterface
	interface EntityUpdateAction<T> {
		void updateEntity(T entity, String value);
	}

	@FunctionalInterface
	interface EntityGetFieldValueAction<T> {
		Object get(T entity);
	}

}
