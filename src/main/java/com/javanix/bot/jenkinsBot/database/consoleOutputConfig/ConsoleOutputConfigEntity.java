package com.javanix.bot.jenkinsBot.database.consoleOutputConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ConsoleOutputConfig")
class ConsoleOutputConfigEntity {

	@Id
	private String id;

	@Indexed(unique = true)
	private String name;

	private String failedTestPattern;
	private String executedTestPattern;
	private String unitTestsResultFilepathPrefix;
	private String fileEncoding;

}
