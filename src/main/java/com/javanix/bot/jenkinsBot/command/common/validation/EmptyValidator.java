package com.javanix.bot.jenkinsBot.command.common.validation;

import com.javanix.bot.jenkinsBot.core.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmptyValidator implements Validator<String> {

	@Override
	public void validate(String fieldValue, List<String> errors, String validationMessageKey) {
		if (fieldValue == null || fieldValue.trim().isEmpty()) {
			errors.add(validationMessageKey);
		}
	}
}
