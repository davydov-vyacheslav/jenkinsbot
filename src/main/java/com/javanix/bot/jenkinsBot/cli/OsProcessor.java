package com.javanix.bot.jenkinsBot.cli;

import org.springframework.stereotype.Component;

@Component
class OsProcessor {

    private static Os os = null;

    public Os getOS() {
        if (os == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = Os.WINDOWS;
            } else {
                os = Os.LINUX;
            }
        }
        return os;
    }
}
