package com.javanix.bot.jenkinsBot.cli.healthstatus;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HealthCheckProcessor {

	@SneakyThrows
	public HealthStatus getHealthStatusForUrl(String urlValue) {
		URL url = new URL(urlValue);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET"); // FIXME: HEAD ?
		connection.setConnectTimeout(5000);
		try {
			connection.connect();
		} catch (IOException ioe) {
			return HealthStatus.DOWN;
		}
		return HealthStatus.of(connection.getResponseCode());
	}
}
