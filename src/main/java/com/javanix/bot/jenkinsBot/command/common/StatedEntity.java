package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatedEntity<T extends Entity> {

	final T entityDto;

	EntityState<T> state;

}
