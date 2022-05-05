package com.javanix.bot.jenkinsBot.core.validation;

import java.util.List;

public interface Validator {

	void validate(String fieldValue, List<String> errors, String validationMessageKey);
}
