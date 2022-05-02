package com.javanix.bot.jenkinsBot;

import com.javanix.bot.jenkinsBot.command.CommandFactory;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private TelegramBotWrapper bot;

    @Autowired
    private CommandFactory commandFactory;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {

        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                Message message = update.message();
                CallbackQuery callbackQuery = update.callbackQuery();
                User from = null;
                String text = null;
                if (message != null) {
                    text = message.text();
                    from = message.from();
                } else if (callbackQuery != null) {
                    text = callbackQuery.data();
                    from = callbackQuery.from();
                }
                if (text == null) {
                    text = "";
                }

                if (message == null && callbackQuery != null) {
                    message = callbackQuery.message();
                }
                if (message != null) {
                    try {
                        commandFactory.getCommand(text).process(message.chat(), from, text);
                    } catch (Exception e) {
                        bot.execute(new SendMessage(message.chat().id(), "Error: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}
