package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.cli.HealthStatus;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.CommandTestConfiguration;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig
@ContextConfiguration(classes = CommandTestConfiguration.class)
public class StatusCommandTest extends AbstractCommandTestCase {

	@MockBean
	private CliProcessor cliProcessor;

	@Test
	public void status_ok() {
		HealthCheckInfoDto endpoint1 = HealthCheckInfoDto.builder()
				.endpointName(ENTITY_NAME)
				.endpointUrl(ENTITY_URL)
				.creatorId(HealthCheckService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.build();
		HealthCheckInfoDto endpoint2 = HealthCheckInfoDto.builder()
				.endpointName(ENTITY_NAME_2)
				.endpointUrl(ENTITY_URL_2)
				.creatorId(HealthCheckService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.build();

		Mockito.when(healthCheckService.getAvailableEndpoints(EntityService.DEFAULT_CREATOR_ID))
				.thenReturn(Arrays.asList(endpoint1, endpoint2));
		Mockito.when(cliProcessor.getHealthStatusForUrl(endpoint1.getEndpointUrl())).thenReturn(HealthStatus.SUCCESS);
		Mockito.when(cliProcessor.getHealthStatusForUrl(endpoint2.getEndpointUrl())).thenReturn(HealthStatus.UNSTABLE);

		String commandText = "/healthcheck status";
		User from = new User(HealthCheckService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			SendMessage message = invocation.getArgument(0);
			assertEquals("message.command.healthcheck.common.list.prefix\n" +
					"message.command.healthcheck.common.status.info\n" +
					"message.command.healthcheck.common.status.info\n", message.getParameters().get("text"));
			return sendResponse;
		});

		// FIXME: parameters
		// TODO: EditMessage ?

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));

	}

}
