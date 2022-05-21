package com.javanix.bot.jenkinsBot.core.model;

import java.util.Set;

public interface Entity {

	boolean isPublic();

	String getName();

	Long getCreatorId();

	void setCreatorId(Long creatorId);

	void setCreatorFullName(String creatorFullName);

	Set<Long> getReferences();
}
