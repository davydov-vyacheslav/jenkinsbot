package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.Entity;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface EntityService<T extends Entity> {

	long DEFAULT_CREATOR_ID = -1L;

	/**
	 * Delete entity/reference by owner and entity name. Return status of removal (ok = true / fail = false)
	 */
	default boolean removeEntity(Long ownerId, String name) {
		T ownedOrReferencedEntity = filter(this::getOwnedOrReferencedEntities, ownerId, name).orElse(null);
		if (ownedOrReferencedEntity == null) {
			return false;
		}
		// if I'm not entity owner -> just remove my reference
		if (!ownedOrReferencedEntity.getCreatorId().equals(ownerId)) {
			ownedOrReferencedEntity.getReferences().remove(ownerId);
			save(ownedOrReferencedEntity);
			return true;
		}
		// if entity is mine and there are no references -> remove at, otherwise transfer ownership to default user
		// (don't make subscribed users argue of lost record)
		if (!ownedOrReferencedEntity.getReferences().isEmpty()) {
			ownedOrReferencedEntity.setCreatorId(DEFAULT_CREATOR_ID);
			ownedOrReferencedEntity.setCreatorFullName("Orphan");
			save(ownedOrReferencedEntity);
		} else {
			removeEntityInternal(ownerId, name);
		}
		return true;
	}

	default Optional<T> filter(Function<Long, Stream<T>> function, Long ownerId, String entityName) {
		return function.apply(ownerId)
				.filter(entityDto -> entityDto.getName().equalsIgnoreCase(entityName))
				.findAny();
	}

	// TODO: + addReferencedUser / removeFromReferencedUser
	void save(T entity);
	void removeEntityInternal(Long ownerId, String name);

	/**
	 * List of owned entities. Actually, this is an editable list of entities.
	 * (As per requirements user is unable to edit referenced entities)
	 */
	Stream<T> getOwnedEntities(Long ownerId);

	/**
	 * List of applicable entites for 'add_reference' action:
	 * - not owned
	 * - not referenced yet
	 * - public
	 */
	Stream<T> getAvailableEntitiesToReference(Long ownerId);

	/**
	 * List of entities, that user is able to get status:
	 * - owned
	 * - referenced by
	 */
	Stream<T> getOwnedOrReferencedEntities(Long ownerId);

	boolean hasEntity(String name);
}
