package com.javanix.bot.jenkinsBot.command.build.add;

import com.javanix.bot.jenkinsBot.database.BuildRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoAddInformation {
    private StateType state;
    private BuildRepository repo;
}
