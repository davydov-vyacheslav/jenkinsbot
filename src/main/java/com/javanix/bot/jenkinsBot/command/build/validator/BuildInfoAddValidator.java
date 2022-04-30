package com.javanix.bot.jenkinsBot.command.build.validator;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuildInfoAddValidator implements Validator {

	private final BuildInfoService database;

	@Override
	public boolean validate(BuildInfoDto target, List<String> errors) {
		Validator.super.validate(target, errors);

		if (database.hasRepository(target.getRepoName())) {
			errors.add("Repo Name is invalid (or not unique)");
		}

		return errors.isEmpty();
	}


}
