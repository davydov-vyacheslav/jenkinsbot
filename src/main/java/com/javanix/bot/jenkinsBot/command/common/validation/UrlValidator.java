package com.javanix.bot.jenkinsBot.command.common.validation;

import com.javanix.bot.jenkinsBot.core.validation.Validator;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Component
public class UrlValidator implements Validator<String> {

	@Override
	public void validate(String fieldValue, List<String> errors, String validationMessageKey) {
		try {
			if (fieldValue != null && !fieldValue.isEmpty()) {
				new URL(fieldValue).toURI();
			}
		} catch (MalformedURLException | URISyntaxException e) {
			errors.add(validationMessageKey);
		}
	}
}
