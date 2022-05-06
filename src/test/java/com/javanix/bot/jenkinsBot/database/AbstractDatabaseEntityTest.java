package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractDatabaseEntityTest<T extends Entity> {

	protected static final Long CURRENT_USER_ID = 1L;
	protected static final Long SOMEONE_USER_ID = 111L;

	@Test
	public void testGetOwnedEntities() {
		List<T> ownedEntities = getDatabaseService().getOwnedEntities(CURRENT_USER_ID);
		assertEquals(2, ownedEntities.size());
		assertThat(Arrays.asList("owned-public", "owned-private"))
				.containsExactlyInAnyOrderElementsOf(ownedEntities.stream().map(Entity::getName).collect(Collectors.toList()));
	}

	protected abstract EntityService<T> getDatabaseService();

}
