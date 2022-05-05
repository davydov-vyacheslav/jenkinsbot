package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.Entity;

import java.util.List;
import java.util.Optional;

public interface EntityService<T extends Entity> {
	void save(T entity);

	Optional<T> getOwnedEntityByName(String name, Long ownerId);

	boolean hasEntity(String name);

	void removeEntity(String name);

	List<T> getOwnedEntities(Long ownerId);
}
