package com.javanix.bot.jenkinsBot.command;

public interface TelegramCommand extends Processable {

    // TODO: make List<String> getCommandNames()
    String getCommandName();
}
