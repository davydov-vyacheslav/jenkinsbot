package com.javanix.bot.jenkinsBot.database.healthcheck;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document(collection = "Endpoint")
class HealthCheckEntity {

	@Id
	private String id;

	@Indexed(unique = true)
	private String endpointName;

	private String endpointUrl;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;

}
