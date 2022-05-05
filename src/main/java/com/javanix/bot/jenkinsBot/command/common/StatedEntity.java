package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.core.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatedEntity<DTO extends Entity> {

	DTO entityDto;

	EntityState<DTO> state;

}
