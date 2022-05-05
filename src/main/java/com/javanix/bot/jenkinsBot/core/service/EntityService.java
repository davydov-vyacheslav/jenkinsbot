package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.Entity;

import java.util.Optional;

public interface EntityService<T extends Entity> {
	void save(T entity);

	Optional<T> getOwnedEntityByName(String name, Long ownerId);
}
