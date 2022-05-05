package com.javanix.bot.jenkinsBot.database.user;

import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "Users")
class UserEntity {

	@Id
	private String id;

	@Indexed(unique = true)
	private Long userId;

	private String userName;
	private LocaleType locale;
	private Map<EntityType, Integer> lastMessageIdMap;
	private List<String> subscribedBuildRepositories;

}
