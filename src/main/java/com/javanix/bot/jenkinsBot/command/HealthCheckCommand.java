package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.common.CommandFactory;
import com.javanix.bot.jenkinsBot.command.healthcheck.HealthCheckCommandFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
@RequiredArgsConstructor
class HealthCheckCommand extends AbstractEntityCommand {

	private final HealthCheckCommandFactory healthCheckCommandFactory;
	private static final Pattern healthCheckCommandPattern = Pattern.compile(".*/healthcheck.?(add|edit|status|delete|my_list).?(.*)", CASE_INSENSITIVE);

	@Override
	public String getCommandName() {
		return "/healthcheck";
	}

	@Override
	protected Pattern getCommandPattern() {
		return healthCheckCommandPattern;
	}

	@Override
	protected CommandFactory getCommandFactory() {
		return healthCheckCommandFactory;
	}

}
