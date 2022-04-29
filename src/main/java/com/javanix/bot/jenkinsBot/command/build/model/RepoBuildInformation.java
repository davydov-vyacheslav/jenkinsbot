package com.javanix.bot.jenkinsBot.command.build.model;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoBuildInformation {
    private StateType state;
    private BuildInfoDto repo;

    public static final String ICON_NA = "\uD83D\uDEAB";

    public String getRepositoryDetails() {
        return "Current repository info:" +
                "\n- repoName: " + getStringOrIcon(repo.getRepoName()) +
                "\n- jenkinsDomain: " + getStringOrIcon(repo.getJenkinsInfo().getDomain()) +
                "\n- jenkinsUser: " + getStringOrIcon(repo.getJenkinsInfo().getUser()) +
                "\n- jenkinsPassword: " + getStringOrIcon(repo.getJenkinsInfo().getPassword()) +
                "\n- jobName: " + getStringOrIcon(repo.getJenkinsInfo().getJobName()) +
                "\n- isPublic: " + repo.getIsPublic();
    }

    private String getStringOrIcon(String value) {
        return (value == null || value.isEmpty()) ? ICON_NA : value;
    }

}
