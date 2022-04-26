package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.javanix.bot.jenkinsBot.core.service.BuildInfoService.DEFAULT_CREATOR_ID;
import static com.javanix.bot.jenkinsBot.core.service.SettingsService.KEY_DB_VERSION;

@Component
@RequiredArgsConstructor
@Log4j2
public class MigrationTool {

	private final BuildInfoService service;
	private final SettingsService settingsService;

	@PostConstruct
	public void init() {
		createOotbData();
	}

	// add OTB data if they are absent
	private void createOotbData() {
		if (service.isDatabaseEmpty()) {
			log.info("Adding OOTB values");
			service.addRepository(BuildInfoDto.builder()
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.repoName("front_runners")
					.jenkinsInfo(JenkinsInfoDto.builder()
							.domain("dev-rim-fr01")
							.user("admin")
							.password("#FrTeam@JenkinsAdmin22!")
							.jobName("Insight")
							.build())
					.build());

			service.addRepository(BuildInfoDto.builder()
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.repoName("finely_blended")
					.jenkinsInfo(JenkinsInfoDto.builder()
							.domain("dev-rim-fb01")
							.user("admin")
							.password("#FbTeam@JenkinsAdmin22!")
							.jobName("Insight")
							.build())
					.build());

			service.addRepository(BuildInfoDto.builder()
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.repoName("xmen")
					.jenkinsInfo(JenkinsInfoDto.builder()
							.domain("dev-rim-xm01")
							.user("admin")
							.password("#XmTeam@JenkinsAdmin22!")
							.jobName("Insight")
							.build())
					.build());

			service.addRepository(BuildInfoDto.builder()
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.repoName("wizards")
					.jenkinsInfo(JenkinsInfoDto.builder()
							.domain("dev-rim-wiz01")
							.user("admin")
							.password("#WizTeam@JenkinsAdmin22!")
							.jobName("Insight")
							.build())
					.build());

			service.addRepository(BuildInfoDto.builder()
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.repoName("gunbros")
					.jenkinsInfo(JenkinsInfoDto.builder()
							.domain("dev-rim-gb01")
							.user("admin")
							.password("#GbTeam@JenkinsAdmin22!")
							.jobName("Insight")
							.build())
					.build());

			service.addRepository(BuildInfoDto.builder()
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.repoName("chefs")
					.jenkinsInfo(JenkinsInfoDto.builder()
							.domain("dev-rim-chf01")
							.user("admin")
							.password("#ChfTeam@JenkinsAdmin22!")
							.jobName("Insight")
							.build())
					.build());
			settingsService.saveSettings(KEY_DB_VERSION, "1");
		}
	}
}
