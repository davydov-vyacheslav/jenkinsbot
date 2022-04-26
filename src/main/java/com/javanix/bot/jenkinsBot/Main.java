package com.javanix.bot.jenkinsBot;

import com.javanix.bot.jenkinsBot.command.CommandFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private CommandFactory commandFactory;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        TelegramBot bot = new TelegramBot(botToken);

        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                Message message = update.message();
                if (message != null) {
                    try {
                        String text = message.text();
                        if (text == null) {
                            text = "";
                        }
                        commandFactory.getCommand(text).process(bot, message);
                    } catch (Exception e) {
                        bot.execute(new SendMessage(message.chat().id(), e.getMessage()));
                        e.printStackTrace();
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}
