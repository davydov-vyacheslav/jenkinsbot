package com.javanix.bot.jenkinsBot.command.common.validation;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import com.javanix.bot.jenkinsBot.core.validation.Validator;
import com.javanix.bot.jenkinsBot.database.DatabaseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UniqueValidator implements Validator<Entity> {

	private final DatabaseFactory databaseFactory;

	@Override
	public void validate(Entity entity, List<String> errors, String validationMessageKey) {
		if (databaseFactory.getDatabase(entity).hasEntity(entity.getName())) {
			errors.add(validationMessageKey);
		}
	}

}
