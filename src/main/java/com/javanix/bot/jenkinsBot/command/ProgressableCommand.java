package com.javanix.bot.jenkinsBot.command;

// Command that contains states for question-answers actions
public interface ProgressableCommand extends Processable {

	boolean isInProgress(Long userId);
}
