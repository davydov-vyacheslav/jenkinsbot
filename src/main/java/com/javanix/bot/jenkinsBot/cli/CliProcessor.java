package com.javanix.bot.jenkinsBot.cli;

import com.javanix.bot.jenkinsBot.database.BuildRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class CliProcessor {

    private final OsProcessor osProcessor;

    private final String baseUrlTemplate = "curl -s --user %s:%s http://%s:7331/job/%s";
    private final String failedTestsUrlTemplate = baseUrlTemplate + "/lastBuild/consoleText | grep \"Test FAILED\"";
    private final String failedTestsTopNUrlTemplate = failedTestsUrlTemplate + " | head -n %d";
    private final String failedTestsCountUrlTemplate = failedTestsUrlTemplate + " | wc -l";
    private final String runTestsUrlTemplate = baseUrlTemplate + "/%s/consoleText | grep \"Tests run\" | wc -l";

    public Integer getFailedTestsCount(BuildRepository team) {
        return Integer.valueOf(executeCommand(String.format(failedTestsCountUrlTemplate,
                team.getJenkinsUser(), team.getJenkinsPassword(), team.getJenkinsDomain(), team.getJobName())));
    }

    public String getFailedTests(BuildRepository team, int count) {
        return executeCommand(String.format(failedTestsTopNUrlTemplate,
                team.getJenkinsUser(), team.getJenkinsPassword(), team.getJenkinsDomain(), team.getJobName(), count));
    }

    public Integer getCurrentRunTestsCount(BuildRepository team) {
        return Integer.valueOf(executeCommand(String.format(runTestsUrlTemplate,
                team.getJenkinsUser(), team.getJenkinsPassword(), team.getJenkinsDomain(), team.getJobName(),
                "lastBuild")));
    }

    public Integer getLastRunTestsCount(BuildRepository team) {
        return Integer.valueOf(executeCommand(String.format(runTestsUrlTemplate,
                team.getJenkinsUser(), team.getJenkinsPassword(), team.getJenkinsDomain(), team.getJobName(),
                "lastCompletedBuild")));
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
