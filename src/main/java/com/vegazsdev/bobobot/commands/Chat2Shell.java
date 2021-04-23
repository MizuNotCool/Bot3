package com.vegazsdev.bobobot.commands;

import com.vegazsdev.bobobot.TelegramBot;
import com.vegazsdev.bobobot.core.Command;
import com.vegazsdev.bobobot.db.PrefObj;
import com.vegazsdev.bobobot.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@SuppressWarnings("unused")
public class Chat2Shell extends Command {

    private static final Logger logger = LoggerFactory.getLogger(Download2GDrive.class);

    public Chat2Shell() {
        super("shell", "Run shell (bash) commands via chat");
    }

    @Override
    public void botReply(Update update, TelegramBot bot, PrefObj prefs) {
        if (update.getMessage().getFrom().getId() == Float.parseFloat(Objects.requireNonNull(Config.getDefConfig("bot-master")))) {
            String msg = update.getMessage().getText().substring(7);
            ProcessBuilder pb;
            pb = new ProcessBuilder("/bin/bash", "-c", msg);
            StringBuilder fullLogs = new StringBuilder();
            fullLogs.append("***$ ").append(msg).append("***\n");
            int id = bot.sendReply(fullLogs.toString(), update);
            try {
                pb.redirectErrorStream(true);
                Process process = pb.start();
                InputStream is = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    fullLogs.append("`").append(line).append("`").append("\n");
                    bot.editMessage(fullLogs.toString(), update, id);
                }
            } catch (Exception e) {
                bot.sendMessage(prefs.getString("something_went_wrong"), update);
                logger.error(e.getMessage(), e);
            }
        }
    }
}