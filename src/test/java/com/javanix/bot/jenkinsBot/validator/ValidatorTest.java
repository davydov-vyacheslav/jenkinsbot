package com.javanix.bot.jenkinsBot.validator;


import com.javanix.bot.jenkinsBot.command.common.validation.EmptyValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.UniqueValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.UrlValidator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.database.DatabaseFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = {ValidatorTest.ValidatorConfiguration.class})
public class ValidatorTest {

	@Autowired
	private EmptyValidator emptyValidator;

	@Autowired
	private UniqueValidator uniqueValidator;

	@Autowired
	private UrlValidator urlValidator;

	@MockBean
	private DatabaseFactory databaseFactory;

	@MockBean
	private BuildInfoService buildInfoService;

	@Test
	public void emptyValidatorTest() {
		List<String> errors = new ArrayList<>();
		emptyValidator.validate("", errors, "empty.string");
		emptyValidator.validate(null, errors, "null.string");
		emptyValidator.validate(" ", errors, "effective.empty.string");
		emptyValidator.validate("valid", errors, "valid.string");
		Assertions.assertThat(errors).containsExactlyInAnyOrder("empty.string", "null.string", "effective.empty.string");
	}

	@Test
	public void urlValidatorTest() {
		List<String> errors = new ArrayList<>();
		urlValidator.validate("", errors, "empty.string");
		urlValidator.validate(null, errors, "null.string");
		urlValidator.validate("https://domain.com:7331/", errors, "valid.url");
		urlValidator.validate("https://domain.com:qwer/", errors, "invalid.port");
		urlValidator.validate("foo string", errors, "invalid.url");
		urlValidator.validate("https://127.0.0.1/", errors, "valid.url2");
		Assertions.assertThat(errors).containsExactlyInAnyOrder("invalid.port", "invalid.url");
	}

	@Test
	public void uniqueValidatorTest() {
		List<String> errors = new ArrayList<>();
		Mockito.when(databaseFactory.getDatabase(any(BuildInfoDto.class))).then(invocation -> buildInfoService);
		Mockito.when(buildInfoService.hasEntity("exists")).thenReturn(true);
		Mockito.when(buildInfoService.hasEntity("non-exist")).thenReturn(false);
		uniqueValidator.validate(BuildInfoDto.builder().repoName("exists").build(), errors, "invalid.entity");
		uniqueValidator.validate(BuildInfoDto.builder().repoName("non-exists").build(), errors, "valid.entity");
		Assertions.assertThat(errors).containsExactlyInAnyOrder("invalid.entity");
	}

	@TestConfiguration
	@ComponentScan(basePackages = {"com.javanix.bot.jenkinsBot.command.common.validation"})
	static class ValidatorConfiguration {  }

}
