package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.cli.healthstatus.HealthCheckProcessor;
import com.javanix.bot.jenkinsBot.cli.healthstatus.HealthStatus;
import com.javanix.bot.jenkinsBot.command.AbstractCommandTestCase;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.EntityService;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class StatusCommandTest extends AbstractCommandTestCase<HealthCheckInfoDto> {

	@MockBean
	private HealthCheckProcessor healthCheckProcessor;

	@Test
	public void status_ok() {
		HealthCheckInfoDto endpoint1 = getHealthCheckEntity1();
		HealthCheckInfoDto endpoint2 = HealthCheckInfoDto.builder()
				.endpointName(ENTITY_NAME_2)
				.endpointUrl(ENTITY_URL_2)
				.creatorId(HealthCheckService.DEFAULT_CREATOR_ID)
				.isPublic(true)
				.build();

		Mockito.when(healthCheckService.getOwnedOrReferencedEntities(EntityService.DEFAULT_CREATOR_ID))
				.thenReturn(Stream.of(endpoint1, endpoint2));
		Mockito.when(healthCheckProcessor.getHealthStatusForUrl(endpoint1.getEndpointUrl())).thenReturn(HealthStatus.SUCCESS);
		Mockito.when(healthCheckProcessor.getHealthStatusForUrl(endpoint2.getEndpointUrl())).thenReturn(HealthStatus.UNSTABLE);

		String commandText = "/healthcheck status";
		User from = new User(HealthCheckService.DEFAULT_CREATOR_ID);

		Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
			SendMessage message = invocation.getArgument(0);
			assertEquals("message.command.healthcheck.common.list.prefix" +
					"message.command.healthcheck.common.status.info" +
					"message.command.healthcheck.common.status.info", message.getParameters().get("text"));
			return sendResponse;
		});

		// FIXME: parameters
		// TODO: EditMessage ?

		TelegramCommand command = factory.getCommand(commandText);
		command.process(chat, from, commandText);
		Mockito.verify(bot).execute(any(SendMessage.class));

	}

}
