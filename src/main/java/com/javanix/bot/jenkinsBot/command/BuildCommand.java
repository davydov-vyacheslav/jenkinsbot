package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.build.BuildCommandFactory;
import com.javanix.bot.jenkinsBot.command.common.CommandFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
@RequiredArgsConstructor
class BuildCommand extends AbstractEntityCommand {

	private final BuildCommandFactory buildCommandFactory;

	private static final Pattern buildCommandPattern = Pattern.compile(".*/build.?(add|edit|status|delete|my_list).?(.*)", CASE_INSENSITIVE);

	@Override
	public String getCommandName() {
		return "/build";
	}

	@Override
	protected Pattern getCommandPattern() {
		return buildCommandPattern;
	}

	@Override
	protected CommandFactory getCommandFactory() {
		return buildCommandFactory;
	}
}
