package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.common.AbstractModifyEntityCommand;
import com.javanix.bot.jenkinsBot.command.common.StatedEntity;
import com.javanix.bot.jenkinsBot.core.model.Entity;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractProgressableCommandTest<T extends Entity>  extends AbstractCommandTestCase {
	@Autowired
	protected List<AbstractModifyEntityCommand<T>> modifyEntityCommands;

	@AfterEach
	public void tearDown() {
		for (AbstractModifyEntityCommand<T> modifyEntityCommand : modifyEntityCommands) {
			((Map<Long, StatedEntity<T>>) Objects.requireNonNull(ReflectionTestUtils.getField(modifyEntityCommand, "usersInProgress"))).clear();
		}
	}


}
