package com.javanix.bot.jenkinsBot.cli.jenkins;

import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class ConsoleOutputResolver {

	public String convertFailedTestsOutputToFullClassName(ConsoleOutputInfoDto consoleOutputInfo, String origin) {
		String result = origin;
		Matcher m = compilePattern(consoleOutputInfo.getFailedTestPattern()).matcher(origin);

		if (m.find()) {
			result = m.replaceAll("$1");
		}

		return result;
	}

	public boolean isExecutedTest(ConsoleOutputInfoDto consoleOutputInfo, String line) {
		return compilePattern(consoleOutputInfo.getExecutedTestPattern()).matcher(line).matches();
	}

	public boolean isFailedTest(ConsoleOutputInfoDto consoleOutputInfo, String line) {
		return compilePattern(consoleOutputInfo.getFailedTestPattern()).matcher(line).matches();
	}

	@Cacheable("patterns")
	public Pattern compilePattern(String pattern) {
		return Pattern.compile(pattern);
	}

}
