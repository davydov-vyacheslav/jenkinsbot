package com.javanix.bot.jenkinsBot.database;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildRepository {

    private String repoName;
    private String jenkinsDomain;
    private String jenkinsUser;
    private String jenkinsPassword;
    private String jobName;
    private Boolean isPublic;
    private Long creatorId;
    private String creatorFullName;

}
