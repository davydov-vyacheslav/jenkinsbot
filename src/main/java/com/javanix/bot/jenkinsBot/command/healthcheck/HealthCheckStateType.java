package com.javanix.bot.jenkinsBot.command.healthcheck;


import com.javanix.bot.jenkinsBot.command.common.EntityState;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;

import java.util.Arrays;

public enum HealthCheckStateType implements EntityState<HealthCheckInfoDto> {

	PUBLIC("public",
		HealthCheckInfoDto::isPublic,
		(entity, value) -> entity.setIsPublic(Boolean.parseBoolean(value))),
	NAME("name",
			HealthCheckInfoDto::getEndpointName,
			HealthCheckInfoDto::setEndpointName),
	URL("url",
			HealthCheckInfoDto::getEndpointUrl,
			HealthCheckInfoDto::setEndpointUrl);

	private final EntityUpdateAction<HealthCheckInfoDto> updateAction;
	private final EntityGetFieldValueAction<HealthCheckInfoDto> getAction;
	private final String fieldKey;

	HealthCheckStateType(String fieldKey, EntityGetFieldValueAction<HealthCheckInfoDto> getAction, EntityUpdateAction<HealthCheckInfoDto> updateAction) {
		this.updateAction = updateAction;
		this.getAction = getAction;
		this.fieldKey = fieldKey;
	}

	@Override
	public String getFieldKey() {
		return fieldKey;
	}

	@Override
	public Object getValue(HealthCheckInfoDto repo) {
		return getAction.get(repo);
	}

	@Override
	public void updateField(HealthCheckInfoDto repo, String value) {
		updateAction.updateEntity(repo, value);
	}

	public static HealthCheckStateType of(String code) {
		return Arrays.stream(values())
				.filter(stateType -> stateType.fieldKey.equalsIgnoreCase(code))
				.findAny()
				.orElse(null);
	}

}
