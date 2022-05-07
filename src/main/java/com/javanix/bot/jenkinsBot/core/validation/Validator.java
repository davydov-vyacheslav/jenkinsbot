package com.javanix.bot.jenkinsBot.core.validation;

import java.util.List;

public interface Validator<T> {

	void validate(T fieldValue, List<String> errors, String validationMessageKey);
}
