package com.javanix.bot.jenkinsBot.database.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Settings")
class SettingsEntity {

	@Id
	private String id;

	@Indexed(unique = true)
	private String key;

	private String value;

}
