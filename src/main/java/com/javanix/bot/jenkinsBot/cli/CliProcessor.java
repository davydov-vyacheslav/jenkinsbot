package com.javanix.bot.jenkinsBot.cli;

import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class CliProcessor {

	private final OsProcessor osProcessor;

	private static final String baseUrlTemplate = "curl -s --user %s:%s http://%s:7331/job/%s/%s/consoleText -o %s";

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

		log.info("Saving data to: " + tempFile.getAbsoluteFile());

		executeCommand(String.format(baseUrlTemplate,
				jenkinsInfo.getUser(), jenkinsInfo.getPassword(), jenkinsInfo.getDomain(), jenkinsInfo.getJobName(),
				buildType, tempFile.getAbsolutePath()));

		JenkinsBuildDetails details = JenkinsBuildDetails.builder()
				.failedTestsCapacity(count)
				.failedTestsCount(0L)
				.runTestsCount(0L)
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
			});
		}

// TODO: use this instead of curl. Beware of authentification
//        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(FILE_URL).openStream());
//        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
//            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//        }

		return details;
	}

	private String executeCommand(String params) {

		List<String> osCommands = getLaunchCommandList();
		List<String> processBuilderCommands = new ArrayList<>(osCommands);
		processBuilderCommands.add(params);

		ProcessBuilder processBuilder = new ProcessBuilder(processBuilderCommands);
		processBuilder.redirectErrorStream(true);
		StringBuilder processOutput = new StringBuilder();

		try {
			Process process = processBuilder.start();

			try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String readLine;

				while ((readLine = processOutputReader.readLine()) != null) {
					processOutput.append(readLine).append(System.lineSeparator());
				}

				process.waitFor();
			}
		} catch (InterruptedException | IOException ex) {
			processOutput.append(ex.getMessage());
			processOutput.append(Arrays.toString(ex.getStackTrace()));
		}
		return processOutput.toString().trim();
	}

	private List<String> getLaunchCommandList() {
		List<String> osCommands = Arrays.asList("/bin/sh", "-c");

		if (osProcessor.getOS() == Os.WINDOWS) {
			osCommands = Arrays.asList("CMD", "/C");
		}
		return osCommands;
	}

}
