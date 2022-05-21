package com.javanix.bot.jenkinsBot.cli.jenkins;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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

	public BuildStatus getBuildStatus(JenkinsInfoDto jenkinsInfo) {

		BuildStatus buildStatus = BuildStatus.IN_PROGRESS;

		try {
			HttpURLConnection httpConnection = configureUrlConnection(jenkinsInfo, jenkinsInfo.getJobUrl() + "/api/json");
			StringWriter sw = new StringWriter();
			try (InputStream in = httpConnection.getInputStream()) {
				IOUtils.copy(in, sw, jenkinsInfo.getConsoleOutputInfo().getFileEncoding());
			}

			JsonObject jsonObject = JsonParser.parseString(sw.toString()).getAsJsonObject();
			int lastBuildObject = getBuildNumber(jsonObject, "lastBuild");
			int lastStableBuildObject = getBuildNumber(jsonObject, "lastStableBuild");
			int lastFailedBuildObject = getBuildNumber(jsonObject, "lastFailedBuild");
			int lastUnstableBuildObject = getBuildNumber(jsonObject, "lastUnstableBuild");
			int lastUnsuccessfulBuildObject = getBuildNumber(jsonObject, "lastUnsuccessfulBuild");

			if (lastBuildObject == lastStableBuildObject) {
				buildStatus = BuildStatus.COMPLETED_OK;
			} else if (lastBuildObject == lastFailedBuildObject) {
				buildStatus = BuildStatus.COMPLETED_FAIL;
			} else if (lastBuildObject == lastUnstableBuildObject) {
				buildStatus = BuildStatus.COMPLETED_UNSTABLE;
			} else if (lastBuildObject == lastUnsuccessfulBuildObject) {
				buildStatus = BuildStatus.COMPLETED_ABORTED;
			}
		} catch (IOException e) {
			buildStatus = BuildStatus.BROKEN;
		}

		return buildStatus;

	}

	private int getBuildNumber(JsonObject jsonObject, String property) {
		return JsonNull.INSTANCE.equals(jsonObject.get(property)) ? 0 : jsonObject.getAsJsonObject(property).getAsJsonPrimitive("number").getAsInt();
	}

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

		HttpURLConnection httpConnection = configureUrlConnection(jenkinsInfo, url);

		try (InputStream in = httpConnection.getInputStream();
			 ReadableByteChannel readableByteChannel = Channels.newChannel(in);
				FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}

		JenkinsBuildDetails details = JenkinsBuildDetails.builder()
				.runTestsCount(0)
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
			});
		}

		details.setBuildStatus(getBuildStatus(jenkinsInfo));
		return details;
	}

	private HttpURLConnection configureUrlConnection(JenkinsInfoDto jenkinsInfo, String url) throws IOException {
		HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
		if (Strings.isNotBlank(jenkinsInfo.getUser())) {
			String userCredentials = jenkinsInfo.getUser() + ":" + jenkinsInfo.getPassword();
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
			httpConnection.setRequestProperty("Authorization", basicAuth);
		}
		return httpConnection;
	}

}
