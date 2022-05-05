package com.javanix.bot.jenkinsBot.command.common.validation;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.core.model.Entity;

import java.util.List;

public interface EntityValidator<T extends Entity> {
	boolean validate(T target, List<String> errors, EntityActionType actionType);
}
