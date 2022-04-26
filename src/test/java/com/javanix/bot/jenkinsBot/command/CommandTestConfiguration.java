package com.javanix.bot.jenkinsBot.command;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = {"com.javanix.bot.jenkinsBot.command", "com.javanix.bot.jenkinsBot.cli"})
public class CommandTestConfiguration {



}
