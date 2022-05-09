package com.javanix.bot.jenkinsBot.cli;

import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Stream;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class CliProcessor {

	public JenkinsBuildDetails getCurrentBuildJenkinsBuildDetails(JenkinsInfoDto jenkinsInfo, int count) {
		return getJenkinsBuildDetails(jenkinsInfo, "lastBuild", count);
	}

	public JenkinsBuildDetails getPreviousBuildJenkinsBuildDetails(JenkinsInfoDto jenkinsInfo) {
		return getJenkinsBuildDetails(jenkinsInfo, "lastCompletedBuild", 0);
	}

	// TODO: cache buildinfo results <info, File>
	@SneakyThrows
	private JenkinsBuildDetails getJenkinsBuildDetails(JenkinsInfoDto jenkinsInfo, String buildType, int count) {
		File tempFile = File.createTempFile("jenkinsbot-", ".log");
		tempFile.deleteOnExit();

		String url = String.format("%s/%s/consoleText", jenkinsInfo.getJobUrl(), buildType);

		log.info(String.format("Saving `%s` data to file: `%s`", url, tempFile.getAbsoluteFile()));

		HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
		String userCredentials = jenkinsInfo.getUser() + ":" + jenkinsInfo.getPassword();
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
		httpcon.setRequestProperty ("Authorization", basicAuth);

		try (InputStream in = httpcon.getInputStream();
			 ReadableByteChannel readableByteChannel = Channels.newChannel(in);
				FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}

		JenkinsBuildDetails details = JenkinsBuildDetails.builder()
				.failedTestsCapacity(count)
				.failedTestsCount(0L)
				.runTestsCount(0L)
				.buildStatus(BuildStatus.IN_PROGRESS)
				.topFailedTests(new ArrayList<>())
				.build();

		// FIXME: encoding wtf'ka
		try(Stream<String> lines = Files.lines(tempFile.toPath(), Charset.forName("windows-1252"))) {
			lines.forEach(s -> {
				if (s.contains("Test FAILED")) {
					details.addFailedTest(s);
				} else if (s.contains("Tests run")) {
					details.incrementRunTestCount();
				}
				details.setBuildStatus(BuildStatus.of(s)); // TODO: use lastLine
			});
		}

		return details;
	}

	@SneakyThrows
	public HealthStatus getHealthStatusForUrl(String urlValue) {
		URL url = new URL(urlValue);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET"); // FIXME: HEAD ?
		connection.setConnectTimeout(5000);
		try {
			connection.connect();
		} catch (IOException ioe) {
			return HealthStatus.DOWN;
		}
		return HealthStatus.of(connection.getResponseCode());
	}

}
