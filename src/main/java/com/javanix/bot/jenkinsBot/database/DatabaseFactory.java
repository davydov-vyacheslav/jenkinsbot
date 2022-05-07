package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseFactory {

	private final Set<EntityService<? extends Entity>> databases;

	public EntityService<? extends Entity> getDatabase(Entity entity) {
		return databases.stream()
				.filter(entityService -> {
					ResolvableType as = ResolvableType.forClass(entityService.getClass()).as(EntityService.class);
					Class<?> genericTypeClass = as.getGeneric(0).getRawClass();
					return genericTypeClass != null && genericTypeClass.equals(entity.getClass());
				})
				.findAny().orElse(null);
	}
}
