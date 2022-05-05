package com.javanix.bot.jenkinsBot.database;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
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
final class MigrationTool {

	private final BuildInfoService service;
	private final HealthCheckService healthCheckService;
	private final SettingsService settingsService;

	@PostConstruct
	public void init() {
		createOotbData();
		fixDomainNames();
	}

	// add OTB data if they are absent
	private void createOotbData() {
		if (service.isDatabaseEmpty()) {
			log.info("Adding OOTB values");
			service.save(BuildInfoDto.builder()
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

			service.save(BuildInfoDto.builder()
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

			service.save(BuildInfoDto.builder()
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

			service.save(BuildInfoDto.builder()
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

			service.save(BuildInfoDto.builder()
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

			service.save(BuildInfoDto.builder()
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

		if (healthCheckService.isDatabaseEmpty()) {
			healthCheckService.save(HealthCheckInfoDto.builder()
					.endpointName("test-dmsmanager-dvt-rim-lb01")
					.endpointUrl("http://dvt-rim-lb01.perceptive.cloud:9091/dmsmanager/actuator/health")
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.build());
			healthCheckService.save(HealthCheckInfoDto.builder()
					.endpointName("test-dmsmanager-10.215.162.170")
					.endpointUrl("http://10.215.162.170:9091/dmsmanager/actuator/health")
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.build());
			healthCheckService.save(HealthCheckInfoDto.builder()
					.endpointName("test-ir-dvt-rim-res01")
					.endpointUrl("http://dvt-rim-res01:2861/InSightRenderingService/Service.svc?wsdl")
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.build());
			healthCheckService.save(HealthCheckInfoDto.builder()
					.endpointName("test-ir-vlqt-53-0587")
					.endpointUrl("http://vlqt-53-0587:2861/InSightRenderingService/Service.svc?wsdl")
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.build());
			healthCheckService.save(HealthCheckInfoDto.builder()
					.endpointName("test-ir-wrong-host")
					.endpointUrl("http://vlqt-53-0588:2861/InSightRenderingService/Service.svc?wsdl")
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.build());
			healthCheckService.save(HealthCheckInfoDto.builder()
					.endpointName("test-ir-wrong-port")
					.endpointUrl("http://vlqt-53-0587:2862/InSightRenderingService/Service.svc?wsdl")
					.creatorFullName("auto")
					.creatorId(DEFAULT_CREATOR_ID)
					.isPublic(true)
					.build());
		}

	}

	public void fixDomainNames() {
		if ("1".equals(settingsService.getSetting(KEY_DB_VERSION))) {
			log.info("Fixing domain names to be `.perceptive.cloud` ... ");
			service.allRepositories()
					.stream()
					.filter(buildInfoDto -> !buildInfoDto.getJenkinsInfo().getDomain().contains("."))
					.peek(buildInfoDto -> buildInfoDto.getJenkinsInfo().setDomain(buildInfoDto.getJenkinsInfo().getDomain() + ".perceptive.cloud"))
					.peek(buildInfoDto -> log.info("Fixed: " + buildInfoDto.getJenkinsInfo().getDomain()))
					.forEach(service::save);
			settingsService.saveSettings(KEY_DB_VERSION, "2");
		}
	}
}
