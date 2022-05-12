package com.javanix.bot.jenkinsBot.cli.jenkins;

import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class JenkinsProcessor {

	private final ConsoleOutputResolver consoleOutputResolver;

	public JenkinsBuildDetails getCurrentBuildJenkinsBuildDetails(JenkinsInfoDto jenkinsInfo) {
		return getJenkinsBuildDetails(jenkinsInfo, "lastBuild");
	}

	public JenkinsBuildDetails getPreviousBuildJenkinsBuildDetails(JenkinsInfoDto jenkinsInfo) {
		return getJenkinsBuildDetails(jenkinsInfo, "lastCompletedBuild");
	}

	public String getTestDetailsUrl(JenkinsInfoDto jenkinsInfo, String testName) {
		return String.format("%s/ws/%s%s.xml/*view*/", jenkinsInfo.getJobUrl(), jenkinsInfo.getConsoleOutputInfo().getUnitTestsResultFilepathPrefix(), testName);
	}

	@SneakyThrows
	private JenkinsBuildDetails getJenkinsBuildDetails(JenkinsInfoDto jenkinsInfo, String buildType) {
		File tempFile = File.createTempFile("jenkinsbot-", ".log");
		tempFile.deleteOnExit();

		String url = String.format("%s/%s/consoleText", jenkinsInfo.getJobUrl(), buildType);

		log.info(String.format("Saving `%s` data to file: `%s`", url, tempFile.getAbsoluteFile()));

		HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
		if (Strings.isNotBlank(jenkinsInfo.getUser())) {
			String userCredentials = jenkinsInfo.getUser() + ":" + jenkinsInfo.getPassword();
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
			httpcon.setRequestProperty("Authorization", basicAuth);
		}

		try (InputStream in = httpcon.getInputStream();
			 ReadableByteChannel readableByteChannel = Channels.newChannel(in);
				FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}

		JenkinsBuildDetails details = JenkinsBuildDetails.builder()
				.runTestsCount(0L)
				.buildStatus(BuildStatus.IN_PROGRESS)
				.failedTests(new LinkedHashSet<>())
				.build();

		ConsoleOutputInfoDto consoleOutputInfo = jenkinsInfo.getConsoleOutputInfo();

		try(Stream<String> lines = Files.lines(tempFile.toPath(), Charset.forName(consoleOutputInfo.getFileEncoding()))) {
			lines.forEach(s -> {
				if (consoleOutputResolver.isFailedTest(consoleOutputInfo, s)) {
					String failedTestName = consoleOutputResolver.convertFailedTestsOutputToFullClassName(consoleOutputInfo, s);
					details.addFailedTest(failedTestName);
				}
				if (consoleOutputResolver.isExecutedTest(consoleOutputInfo, s)) {
					details.incrementRunTestCount();
				}
				details.setBuildStatus(BuildStatus.of(s)); // TODO: use lastLine
			});
		}

		return details;
	}

}
