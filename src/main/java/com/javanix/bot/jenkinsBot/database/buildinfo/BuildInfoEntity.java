package com.javanix.bot.jenkinsBot.database.buildinfo;

import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document(collection = "BuildInfo")
class BuildInfoEntity {

	@Id
	private String id;

	@Indexed(unique = true)
	private String repoName;

	private JenkinsInfoDto jenkinsInfo;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;

}
