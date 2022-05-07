package com.javanix.bot.jenkinsBot.core.validation;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.core.model.Entity;

import java.util.List;

public interface EntityValidator<T extends Entity> {
	// TODO: get rid of EntityActionType as it belongs to another package/layer
	boolean validate(T target, List<String> errors, EntityActionType actionType);
}
